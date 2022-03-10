from sqlalchemy.orm import Session

from app import schemas, models


def create_user_data(db: Session, user_data: schemas.UserDataCreate, user_id: str):
    db_user_data = models.UserData(uid=user_id, **user_data.dict())
    db.add(db_user_data)
    db.commit()
    db.refresh(db_user_data)
    return db_user_data


def get_user_data(db: Session, user_id: str):
    return db.query(models.UserData).filter_by(uid=user_id).first()


def delete_user_data(db: Session, user_id: str):
    user_data = get_user_data(db, user_id)
    db.query(models.UserData).filter_by(uid=user_id).delete()
    db.commit()
    return user_data


def update_user_data(db: Session, user_data_update: schemas.UserDataUpdate, user_id: str):
    user_data = db.query(models.UserData).filter_by(uid=user_id)
    user_data.update(user_data_update.dict(exclude_unset=True))
    db.commit()
    return get_user_data(db, user_id)
