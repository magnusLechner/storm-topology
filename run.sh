#!/usr/bin/env bash
set -e

docker run -p 3772:3772 -p 8080:8080 -t -i eventstorm/sentistorm
