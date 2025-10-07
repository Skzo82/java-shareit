#!/usr/bin/env bash
set -euo pipefail

docker compose up -d
echo "Docker is up"

./tests/.github/workflows/wait-for-it.sh localhost:9090 -t 180 || true

i=0
until curl -fsS http://localhost:9090/actuator/health >/dev/null; do
  i=$((i+1))
  if [ "$i" -gt 90 ]; then
    echo "Server not healthy in time. Dumping logs…"
    docker compose logs --no-color shareit-server || true
    exit 124
  fi
  sleep 2
done

echo "Server is healthy"
