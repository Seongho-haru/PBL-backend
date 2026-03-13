# Multi-stage build for PBL-Backend application
# Build stage: Debian 기반, multi-arch
FROM gradle:8.5-jdk17 AS builder

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

# Production stage: Ubuntu Jammy JRE, multi-arch(ARM64 포함)
FROM eclipse-temurin:17-jre-jammy AS production

# Install required packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    bash \
    sudo \
 && rm -rf /var/lib/apt/lists/*

# Create runtime user
RUN useradd -m -u 1000 -s /bin/bash pbl

# Set up sudo for the runtime user (needed for Docker operations)
RUN echo "pbl ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/pbl

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# (보통 application.yml은 JAR에 포함됩니다. 외부 설정으로 덮어쓸 때만 사용)
# COPY --from=builder /app/src/main/resources/application.yml application.yml

# Create necessary directories
RUN mkdir -p /app/logs /app/tmp && \
    chown -R pbl:pbl /app
USER pbl

# Expose port
EXPOSE 2358

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
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    vim \
    htop \
 && rm -rf /var/lib/apt/lists/*
USER pbl
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=development -jar app.jar"]
