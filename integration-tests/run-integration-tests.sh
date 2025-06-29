#!/bin/bash

echo "Starting integration test environment..."
docker-compose -f docker-compose.test.yml up -d

echo "Waiting for services to be ready..."
sleep 10

echo "Running integration tests..."
mvn test -Dtest="*IntegrationTest"

echo "Stopping test environment..."
docker-compose -f docker-compose.test.yml down

echo "Integration tests complete!"
