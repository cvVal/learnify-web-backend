#!/usr/bin/env bash

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

YAML_SPECS="$(find "${SCRIPT_DIR}/open-api" -name '*.yaml')"

COUNT=0
PRIMARY=0

# shellcheck disable=SC2068
for SPEC in ${YAML_SPECS[@]}
do
  COUNT=$((COUNT+=1))
  URLS="${URLS},{ url: '${SPEC//$SCRIPT_DIR/}', name: '$COUNT' }"
  if [[ "$SPEC" == *learnify-api.yaml ]]; then
    PRIMARY=$COUNT
  fi
done

export URLS_PRIMARY_NAME="${PRIMARY}"
export URLS="[${URLS:1}]"
