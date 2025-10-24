#!/usr/bin/env bash
# Simplified Maven wrapper that delegates to the Maven installation available on PATH.
# Use the MVNW_MAVEN binary override to point to a custom Maven executable when needed.
set -euo pipefail

MVN_BIN="${MVNW_MAVEN:-mvn}"
exec "$MVN_BIN" "$@"
