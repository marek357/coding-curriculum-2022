#!/bin/bash
cd hmsensyne
docker-compose -f docker-compose.test.yaml build
docker-compose -f docker-compose.test.yaml run app pytest
docker-compose down
