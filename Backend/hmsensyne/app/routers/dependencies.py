from fastapi import Depends, HTTPException, Response, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from firebase_admin import auth, credentials
from app.database import SessionLocal
import firebase_admin

cred = credentials.Certificate('/app/app/resources/account_key.json')
firebase_admin.initialize_app(cred)


def get_user(response: Response, bearer_token: HTTPAuthorizationCredentials = Depends(HTTPBearer(auto_error=False))):
    if not bearer_token:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="HTTP Bearer token missing"
        )
    try:
        verified_token = auth.verify_id_token(bearer_token.credentials)
    except Exception as err:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Firebase authorization failed. Reason: {err}"
        )
    response.headers['WWW-Authenticate'] = 'Bearer realm="auth_required"'
    return verified_token


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
