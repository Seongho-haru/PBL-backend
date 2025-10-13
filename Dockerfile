# Multi-stage build for Judge0 Spring Boot application
FROM --platform=${BUILD_PLATFORM:-linux/amd64} gradle:8.5-jdk17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build application
RUN gradle build -x test --no-daemon

# Production stage
FROM --platform=${BUILD_PLATFORM:-linux/amd64} eclipse-temurin:17-jdk-alpine AS production

# Install required packages
RUN apk add --no-cache \
    curl \
    bash \
    sudo \
    shadow

# Create judge0 user
RUN addgroup -g 1000 judge0 && \
    adduser -u 1000 -G judge0 -s /bin/bash -h /app -D judge0

# Set up sudo for judge0 user (needed for Docker operations)
RUN echo "judge0 ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/judge0

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy additional configuration files if needed
COPY --from=builder /app/src/main/resources/application.yml application.yml

# Create necessary directories
RUN mkdir -p /app/logs && \
    mkdir -p /app/tmp && \
    chown -R judge0:judge0 /app

# Expose port
EXPOSE 2358

# Switch to judge0 user
USER judge0

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:2358/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xmx1g -Xms512m" \
    SPRING_PROFILES_ACTIVE="production"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Development stage
FROM production AS development

USER root

# Install development tools
RUN apk add --no-cache \
    git \
    vim \
    htop

# Switch back to judge0 user
USER judge0

# Override entry point for development
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=development -jar app.jar"]
