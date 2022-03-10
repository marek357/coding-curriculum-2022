import pytest
from fastapi.testclient import TestClient
from app.main import app

from app.routers.dependencies import get_user
from tests.setup_tests import override_get_user

user = {'uid': 'test_user_2'}

app.dependency_overrides = {get_user: override_get_user}


@pytest.fixture(scope="module")
def get_test_client():
    client = TestClient(app)
    yield client


class TestMeasurements:
    @pytest.mark.order(1)
    def test_no_user_data(self, get_test_client):
        response = get_test_client.get('/users', headers={'Authorization': f'Bearer {user["uid"]}'})
        assert response.status_code == 404

    @pytest.mark.order(2)
    def test_create_user_data(self, get_test_client):
        response = get_test_client.post(
            '/user',
            json={
                'user_completed_onboarding': False,
                'name': 'Jane Doe',
                'age': 42
            },
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 201
        assert {
                   'user_completed_onboarding': False,
                   'name': 'Jane Doe',
                   'age': 42
               } == response.json()

    @pytest.mark.order(3)
    def test_get_user_data(self, get_test_client):
        response = get_test_client.get('/user', headers={'Authorization': f'Bearer {user["uid"]}'})
        assert response.status_code == 200
        assert {
                   'user_completed_onboarding': False,
                   'name': 'Jane Doe',
                   'age': 42
               } == response.json()

    @pytest.mark.order(4)
    def test_partial_update_user_data(self, get_test_client):
        response = get_test_client.patch(
            '/user',
            json={
                'user_completed_onboarding': True,
            },
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 200
        assert {
                   'user_completed_onboarding': True,
                   'name': 'Jane Doe',
                   'age': 42
               } == response.json()

    @pytest.mark.order(5)
    def test_delete_user_data(self, get_test_client):
        response = get_test_client.delete(
            '/user',
            params={'uid': user['uid']},
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 200
        assert {
                   'user_completed_onboarding': True,
                   'name': 'Jane Doe',
                   'age': 42
               } == response.json()

    @pytest.mark.order(6)
    def test_user_data_deleted(self, get_test_client):
        self.test_no_user_data(get_test_client)

    @pytest.mark.order(7)
    def test_delete_nonexistent_user_data(self, get_test_client):
        response = get_test_client.delete(
            '/user',
            headers={'Authorization': f'Bearer {user["uid"]}'}
        )
        assert response.status_code == 404
