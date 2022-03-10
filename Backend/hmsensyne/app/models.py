from sqlalchemy import Boolean, Column, Integer, String, ForeignKey, DateTime, Float
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


class UserData(Base):

    __tablename__ = 'user_data'

    uid = Column(String, primary_key=True)
    user_completed_onboarding = Column(Boolean, default=False)
    name = Column(String)
    age = Column(Integer)


class Measurement(Base):

    __tablename__ = 'measurement'

    id = Column(Integer, primary_key=True)
    uid = Column(String, ForeignKey(UserData.uid))
    timestamp = Column(DateTime)
    heart_rate_value = Column(Float, nullable=True)
    blood_pressure_systolic_value = Column(Float, nullable=True)
    blood_pressure_diastolic_value = Column(Float, nullable=True)
