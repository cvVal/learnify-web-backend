#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

source "${SCRIPT_DIR}/stop-swagger-ui.sh"

set -e

source "${SCRIPT_DIR}/generate-swagger-urls.sh"

docker pull swaggerapi/swagger-ui --platform  linux/amd64
docker run --name="swagger" --rm -d -p 80:8080 -e URLS_PRIMARY_NAME="${URLS_PRIMARY_NAME}" -e URLS="${URLS}" -v "${SCRIPT_DIR}/open-api:/usr/share/nginx/html/open-api/" swaggerapi/swagger-ui

docker pull stoplight/prism:3 --platform  linux/amd64
docker run --init --name="swagger-mock" --rm -d -p 4010:4010 stoplight/prism:3 mock -h 0.0.0.0 http://docker.for.mac.localhost/open-api/learnify-api.yaml

echo "Swagger is running with a mock server enabled, use CTL-C to stop containers and quit"
echo ""
echo "Swagger Spec: http://localhost"
echo "Swagger Mock Server: http://localhost:4010"
echo ""

open http://localhost
