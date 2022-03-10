import datetime
from typing import Optional

from sqlalchemy import and_
from sqlalchemy.orm import Session

from app import schemas, models


def create_measurement(db: Session, measurement_data: schemas.MeasurementDataCreate, user_id: str):
    db_measurement_data = models.Measurement(**measurement_data.dict(exclude_unset=True), uid=user_id)
    db.add(db_measurement_data)
    db.commit()
    db.refresh(db_measurement_data)
    return db_measurement_data


def delete_measurement(db: Session, user_id: str, measurement_id: int):
    measurement = get_measurement_by_id(db, user_id, measurement_id)
    db.query(models.Measurement).filter_by(uid=user_id, id=measurement_id).delete()
    db.commit()
    return measurement


def get_measurement(
        db: Session, user_id: str,
        start_date: Optional[datetime.datetime] = None,
        end_date: Optional[datetime.datetime] = None
):
    query = db.query(models.Measurement).filter(models.Measurement.uid == user_id)
    if start_date:
        query.filter(models.Measurement.timestamp >= start_date)
    if end_date:
        query.filter(models.Measurement.timestamp <= end_date)
    return query.all()


def get_measurement_by_id(db: Session, user_id: str, measurement_id: int):
    return db.query(models.Measurement).filter(
        and_(
            models.Measurement.uid == user_id,
            models.Measurement.id == measurement_id
        )
    ).first()
