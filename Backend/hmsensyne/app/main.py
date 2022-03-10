from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import app.routers.users.users as user_router
import app.routers.measurements.measurements as measurements_router


def get_application():
    _app = FastAPI(
        title='hmsensyne',
    )

    _app.add_middleware(
        CORSMiddleware,
        allow_origins=[str(origin) for origin in ['http://localhost:3000']],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    _app.include_router(user_router.router, prefix="/user")
    _app.include_router(measurements_router.router, prefix="/measurements")

    return _app


app = get_application()
