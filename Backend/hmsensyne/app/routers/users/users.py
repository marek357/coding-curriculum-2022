from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.database import engine

from app import schemas, models
from app.routers.dependencies import get_user, get_db
from app.routers.users import crud

models.Base.metadata.create_all(bind=engine)

router = APIRouter(tags=['User'])


@router.post('', response_model=schemas.UserData, status_code=201)
def create_user_data(user_data: schemas.UserDataCreate, user=Depends(get_user), db: Session = Depends(get_db)):
    db_user = crud.get_user_data(db, user_id=user['uid'])
    if db_user:
        raise HTTPException(status_code=400, detail="User already registered")
    return crud.create_user_data(db, user_data, user_id=user['uid'])


@router.get('', response_model=schemas.UserData)
def get_user_data(db: Session = Depends(get_db), user=Depends(get_user)):
    db_user = crud.get_user_data(db, user_id=user['uid'])
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    return db_user


@router.patch('', response_model=schemas.UserData)
def update_user_data(user_data: schemas.UserDataUpdate, db: Session = Depends(get_db), user=Depends(get_user)):
    db_user = crud.get_user_data(db, user_id=user['uid'])
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    return crud.update_user_data(db, user_data, user_id=user['uid'])


@router.delete('', response_model=schemas.UserData)
def delete_user(db: Session = Depends(get_db), user=Depends(get_user)):
    db_user = crud.get_user_data(db, user_id=user['uid'])
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    return crud.delete_user_data(db, user['uid'])
