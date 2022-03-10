from datetime import datetime

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.core.config import settings
from sqlalchemy.ext.declarative import declarative_base
from fastapi import Depends, Response
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer


def setup_tests():
    engine = create_engine(settings.DATABASE_URI, pool_pre_ping=True)
    TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    Base = declarative_base()
    Base.metadata.create_all(bind=engine)


def override_get_user(
        response: Response,
        bearer_token: HTTPAuthorizationCredentials = Depends(HTTPBearer(auto_error=False))
):
    return {'uid': bearer_token.credentials}


timestamp = datetime.now()
timestamp_string = timestamp.strftime('%Y-%m-%dT%H:%M:%S.%f')
