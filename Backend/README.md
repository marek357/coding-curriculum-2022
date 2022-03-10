# hmsensyne backend

This is the implementation of the backend for the hms-sensyne[-ios|-android] applications.

## Deployment

To deploy the backend, simply run the deployment script: `./run_backend.sh`. 

Alternatively, run the following command: `docker compose up --build`

## Testing

To deploy the backend, simply run the test script: `./run_tests.sh`. 

Alternatively, run the following command: `docker compose -f docker-compose.test.yaml run app pytest; docker compose down`

## Endpoint documentation

The backend has a specific URL for endpoint documentation: /docs

The documentation is in the OpenAPI format, and allows for easy endpoint testing. 
Additionally, schemas used within the API, can also be found in the documentation.

## Database administration page

The database can be administered from GUI administration page (pgAdmin), available at the port `5050`. 

Login credentials can be found and/or changed in the docker-compose.yaml file 
`PGADMIN_DEFAULT_EMAIL` and `PGADMIN_DEFAULT_PASSWORD`

## Implementation remarks

The backend is written in FastAPI, the database is in the PostgreSQL technology. 
The project was generated from project template, using the `fastapi startproject` tool.


## License

This project is licensed under the terms of the MIT license.
