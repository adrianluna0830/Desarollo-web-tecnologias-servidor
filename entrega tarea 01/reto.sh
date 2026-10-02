#!/bin/sh
cd -- "$(dirname -- "$0")" || exit 1
SERVER_PORT=8081 exec ./mvnw spring-boot:run
