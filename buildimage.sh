#!/bin/bash
set -eo pipefail
APP_NAME=$1
UPDATE_CACHE=""
docker build -f ECSDockerfile \
   --build-arg ARENA_BUILD_TARGET=$LOGICAL_ENV \
   -t $APP_NAME:latest .