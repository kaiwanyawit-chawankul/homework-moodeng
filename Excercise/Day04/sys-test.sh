#!/bin/bash

docker-compose up redis nginx kafka cassandra scala-backend processing-backend scala-backend2 test --abort-on-container-exit --exit-code-from test
# Capture the exit code of the test container
EXIT_CODE=$?

# Check if the exit code is non-zero
if [ $EXIT_CODE -ne 0 ]; then
  docker-compose down
  echo "Error: Test container exited with code $EXIT_CODE"
  # Optionally, you can exit the script immediately to prevent further actions
  exit $EXIT_CODE
fi
