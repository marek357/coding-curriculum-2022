import datetime
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database import engine

from app import schemas, models
from app.routers.dependencies import get_user, get_db
from app.routers.measurements import crud
from app.routers.users import crud as user_crud

models.Base.metadata.create_all(bind=engine)

router = APIRouter(tags=['Measurements'])


@router.get('', response_model=List[schemas.MeasurementData])
def get_measurements(
        start_date: Optional[datetime.datetime] = None,
        end_date: Optional[datetime.datetime] = None,
        user=Depends(get_user), db: Session = Depends(get_db)
):
    return crud.get_measurement(db, user['uid'], start_date, end_date)


@router.post('', response_model=schemas.MeasurementData, status_code=201)
def create_measurement(measurement_data: schemas.MeasurementDataCreate,
                       db: Session = Depends(get_db), user=Depends(get_user)):
    db_user = user_crud.get_user_data(db, user_id=user['uid'])
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    return crud.create_measurement(db, measurement_data, user_id=user['uid'])


@router.delete('', response_model=schemas.MeasurementData)
def delete_measurement(measurement_id: int,
                       db: Session = Depends(get_db), user=Depends(get_user)):
    db_user = user_crud.get_user_data(db, user_id=user['uid'])
    measurement_data = crud.get_measurement_by_id(db, user['uid'], measurement_id)
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    if not measurement_data:
        raise HTTPException(status_code=404, detail="Measurement not found")
    return crud.delete_measurement(db, user['uid'], measurement_id)
