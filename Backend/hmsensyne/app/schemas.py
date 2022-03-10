import datetime
from typing import Optional

from pydantic import BaseModel


class UserDataBase(BaseModel):
    user_completed_onboarding: bool
    name: str
    age: int


class UserDataCreate(UserDataBase):
    pass


class UserDataUpdate(UserDataBase):
    user_completed_onboarding: Optional[bool]
    name: Optional[str]
    age: Optional[int]

    class Config:
        orm_mode = True


class UserData(UserDataBase):
    class Config:
        orm_mode = True


class UserCompletedOnboarding(BaseModel):
    user_completed_onboarding: bool


class MeasurementDataBase(BaseModel):
    heart_rate_value: Optional[float]
    blood_pressure_systolic_value: Optional[float]
    blood_pressure_diastolic_value: Optional[float]
    timestamp: datetime.datetime


class MeasurementData(MeasurementDataBase):
    id: int

    class Config:
        orm_mode = True


class MeasurementDataCreate(MeasurementDataBase):
    class Config:
        orm_mode = True
