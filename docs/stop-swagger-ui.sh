#!/usr/bin/env bash

set +e

echo "Stopping Swagger servers..."
docker stop "swagger"
docker stop "swagger-mock"
echo "Swagger servers stopped"