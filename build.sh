#!/usr/bin/env bash
set -e

mvn package
docker build -t eventstorm/sentistorm -f src/main/java/at/illecker/sentistorm/Dockerfile .
