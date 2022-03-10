import pytest
from fastapi.testclient import TestClient
from app.main import app
from app.routers.measurements import crud as measurements_crud
from app.routers.users import crud as users_crud

from app.routers.dependencies import get_user, get_db
from app.schemas import MeasurementDataCreate, UserDataCreate
from tests.setup_tests import timestamp, timestamp_string, override_get_user, setup_tests

user = {'uid': 'test_user'}

app.dependency_overrides = {get_user: override_get_user}


@pytest.fixture(scope="module")
def get_test_client():
    client = TestClient(app)
    yield client


setup_complete = False


def setup_module():
    global setup_complete
    if not setup_complete:
        setup_tests()
        db = next(get_db())
        users_crud.create_user_data(
            db,
            UserDataCreate(
                user_completed_onboarding=False,
                name='John Doe',
                age=42
            ),
            user_id=user['uid']
        )
        measurements_crud.create_measurement(
            db,
            MeasurementDataCreate(
                heart_rate_value=60,
                blood_pressure_systolic_value=80,
                blood_pressure_diastolic_value=120,
                timestamp=timestamp
            ),
            user_id=user['uid']
        )
        setup_complete = True


class TestMeasurements:
    @pytest.mark.order(1)
    def test_get_measurements(self, get_test_client):
        response = get_test_client.get('/measurements', headers={'Authorization': f'Bearer {user["uid"]}'})
        assert response.status_code == 200
        assert len(response.json()) == 1
        assert {
                   'id': 1,
                   'heart_rate_value': 60,
                   'blood_pressure_systolic_value': 80,
                   'blood_pressure_diastolic_value': 120,
                   'timestamp': timestamp_string
               } == response.json()[0]

    @pytest.mark.order(2)
    def test_create_measurement(self, get_test_client):
        response = get_test_client.post(
            '/measurements',
            json={
                'heart_rate_value': 65.5,
                'blood_pressure_systolic_value': 90.5,
                'blood_pressure_diastolic_value': 135.5,
                'timestamp': timestamp_string
            },
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 201
        assert {
                   'id': 2,
                   'heart_rate_value': 65.5,
                   'blood_pressure_systolic_value': 90.5,
                   'blood_pressure_diastolic_value': 135.5,
                   'timestamp': timestamp_string
               } == response.json()

    @pytest.mark.order(3)
    def test_get_measurements_after_new_measurement_added(self, get_test_client):
        response = get_test_client.get('/measurements', headers={'Authorization': f'Bearer {user["uid"]}'})
        assert response.status_code == 200
        assert len(response.json()) == 2
        assert [{
            'id': 1,
            'heart_rate_value': 60,
            'blood_pressure_systolic_value': 80,
            'blood_pressure_diastolic_value': 120,
            'timestamp': timestamp_string
        }, {
            'id': 2,
            'heart_rate_value': 65.5,
            'blood_pressure_systolic_value': 90.5,
            'blood_pressure_diastolic_value': 135.5,
            'timestamp': timestamp_string
        }] == response.json()

    @pytest.mark.order(4)
    def test_delete_measurement(self, get_test_client):
        response = get_test_client.delete(
            '/measurements',
            params={'measurement_id': 1},
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 200
        assert {
                   'id': 1,
                   'heart_rate_value': 60,
                   'blood_pressure_systolic_value': 80,
                   'blood_pressure_diastolic_value': 120,
                   'timestamp': timestamp_string
               } == response.json()

    @pytest.mark.order(5)
    def test_get_measurements_after_insertion_removal(self, get_test_client):
        response = get_test_client.get('/measurements', headers={'Authorization': f'Bearer {user["uid"]}'})
        assert response.status_code == 200
        assert len(response.json()) == 1
        assert {
                   'id': 2,
                   'heart_rate_value': 65.5,
                   'blood_pressure_systolic_value': 90.5,
                   'blood_pressure_diastolic_value': 135.5,
                   'timestamp': timestamp_string
               } == response.json()[0]

    @pytest.mark.order(6)
    def test_delete_nonexistent_measurement(self, get_test_client):
        response = get_test_client.delete(
            '/measurements',
            params={'measurement_id': 42},
            headers={'Authorization': 'Bearer mock_token'}
        )
        assert response.status_code == 404

    @pytest.mark.order(7)
    def test_get_measurements_after_incorrect_removal(self, get_test_client):
        self.test_get_measurements_after_insertion_removal(get_test_client)
