#!/bin/bash

# Judge0 Spring Boot Docker Entrypoint Script

set -e

echo "Starting Judge0 Spring Boot..."

# Wait for database to be ready
if [ -n "$POSTGRES_HOST" ]; then
    echo "Waiting for PostgreSQL at $POSTGRES_HOST:${POSTGRES_PORT:-5432}..."
    while ! nc -z $POSTGRES_HOST ${POSTGRES_PORT:-5432}; do
        sleep 1
    done
    echo "PostgreSQL is ready!"
fi

# Wait for Redis to be ready
if [ -n "$REDIS_HOST" ]; then
    echo "Waiting for Redis at $REDIS_HOST:${REDIS_PORT:-6379}..."
    while ! nc -z $REDIS_HOST ${REDIS_PORT:-6379}; do
        sleep 1
    done
    echo "Redis is ready!"
fi

# Set default Java options if not provided
if [ -z "$JAVA_OPTS" ]; then
    JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
fi

# Set default Spring profile if not provided
if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
    SPRING_PROFILES_ACTIVE="production"
fi

echo "Starting application with JAVA_OPTS: $JAVA_OPTS"
echo "Active profile: $SPRING_PROFILES_ACTIVE"

# Start the application
exec java $JAVA_OPTS \
    -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /app/app.jar "$@"
