#!/bin/bash

# Quick Start Script for Judge0 Spring Boot
# This script will get Judge0 Spring Boot running in under 5 minutes

set -e

echo "🚀 Judge0 Spring Boot Quick Start"
echo "================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_step() {
    echo -e "${BLUE}[STEP $1]${NC} $2"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Step 1: Check prerequisites
print_step "1" "Checking prerequisites..."

# Check Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION found"
    else
        print_error "Java 17 or higher required. Current: $JAVA_VERSION"
        echo "Please install Java 17: https://adoptium.net/"
        exit 1
    fi
else
    print_error "Java not found"
    echo "Please install Java 17: https://adoptium.net/"
    exit 1
fi

# Check Docker
if command_exists docker; then
    if docker info >/dev/null 2>&1; then
        print_success "Docker found and running"
    else
        print_error "Docker daemon not running"
        echo "Please start Docker Desktop or Docker daemon"
        exit 1
    fi
else
    print_error "Docker not found"
    echo "Please install Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check Docker Compose
if command_exists docker-compose; then
    print_success "Docker Compose found"
elif docker compose version >/dev/null 2>&1; then
    print_success "Docker Compose (plugin) found"
    # Create alias for older scripts
    alias docker-compose='docker compose'
else
    print_error "Docker Compose not found"
    echo "Please install Docker Compose: https://docs.docker.com/compose/install/"
    exit 1
fi

echo ""

# Step 2: Clone or verify repository
print_step "2" "Setting up project..."

if [ ! -f "build.gradle" ]; then
    print_error "Not in Judge0 Spring Boot directory"
    echo "Please run this script from the judge0-spring project root"
    echo "Or clone the repository first:"
    echo "  git clone https://github.com/judge0/judge0-spring.git"
    echo "  cd judge0-spring"
    exit 1
fi

print_success "Project directory verified"

# Make scripts executable
chmod +x scripts/* docker-entrypoint.sh 2>/dev/null || true
print_success "Scripts made executable"

echo ""

# Step 3: Start dependencies
print_step "3" "Starting database services..."

echo "Starting PostgreSQL and Redis..."
docker-compose up -d db redis

# Wait for services
echo "Waiting for services to be ready..."
sleep 5

# Check PostgreSQL
echo -n "Checking PostgreSQL... "
for i in {1..30}; do
    if docker-compose exec -T db pg_isready -U judge0 -d judge0 >/dev/null 2>&1; then
        print_success "PostgreSQL ready"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "PostgreSQL failed to start"
        echo "Check logs: docker-compose logs db"
        exit 1
    fi
    sleep 2
    echo -n "."
done

# Check Redis
echo -n "Checking Redis... "
for i in {1..15}; do
    if docker-compose exec -T redis redis-cli ping >/dev/null 2>&1; then
        print_success "Redis ready"
        break
    fi
    if [ $i -eq 15 ]; then
        print_error "Redis failed to start"
        echo "Check logs: docker-compose logs redis"
        exit 1
    fi
    sleep 1
    echo -n "."
done

echo ""

# Step 4: Choose execution method
print_step "4" "Choose how to run Judge0 Spring Boot:"
echo ""
echo "1) 🚀 Docker Compose (Recommended - Full stack)"
echo "2) 💻 Local Development (Gradle)"
echo "3) 🐳 Docker only (API container)"
echo ""
echo -n "Enter your choice (1-3): "
read -r choice

case $choice in
    1)
        print_step "4a" "Starting with Docker Compose..."
        
        # Check if we need to build the image
        if ! docker images | grep -q judge0-spring; then
            echo "Building Docker image..."
            docker-compose build judge0-api
        fi
        
        echo "Starting Judge0 Spring Boot..."
        docker-compose up -d judge0-api
        
        # Wait for application
        echo "Waiting for application to start..."
        for i in {1..60}; do
            if curl -s http://localhost:2358/health >/dev/null 2>&1; then
                break
            fi
            if [ $i -eq 60 ]; then
                print_error "Application failed to start"
                echo "Check logs: docker-compose logs judge0-api"
                exit 1
            fi
            sleep 2
            echo -n "."
        done
        ;;
        
    2)
        print_step "4b" "Building and starting locally..."
        
        echo "Building application..."
        ./gradlew build -x test
        
        echo "Starting Judge0 Spring Boot..."
        echo "Note: This will run in the foreground. Press Ctrl+C to stop."
        echo ""
        
        # Set development profile and start
        SPRING_PROFILES_ACTIVE=development ./gradlew bootRun &
        APP_PID=$!
        
        # Wait for application
        echo "Waiting for application to start..."
        for i in {1..60}; do
            if curl -s http://localhost:2358/health >/dev/null 2>&1; then
                break
            fi
            if [ $i -eq 60 ]; then
                print_error "Application failed to start"
                kill $APP_PID 2>/dev/null || true
                exit 1
            fi
            sleep 2
            echo -n "."
        done
        ;;
        
    3)
        print_step "4c" "Building and starting Docker container..."
        
        echo "Building Docker image..."
        docker build -t judge0-spring .
        
        echo "Starting Judge0 Spring Boot container..."
        docker run -d \
            --name judge0-api \
            --network judge0-spring_default \
            -e POSTGRES_HOST=db \
            -e REDIS_HOST=redis \
            -e SPRING_PROFILES_ACTIVE=docker \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -p 2358:2358 \
            -p 8081:8081 \
            --privileged \
            judge0-spring
        
        # Wait for application
        echo "Waiting for application to start..."
        for i in {1..60}; do
            if curl -s http://localhost:2358/health >/dev/null 2>&1; then
                break
            fi
            if [ $i -eq 60 ]; then
                print_error "Application failed to start"
                echo "Check logs: docker logs judge0-api"
                exit 1
            fi
            sleep 2
            echo -n "."
        done
        ;;
        
    *)
        print_error "Invalid choice"
        exit 1
        ;;
esac

echo ""

# Step 5: Verify installation
print_step "5" "Verifying installation..."

# Test health endpoint
if curl -s http://localhost:2358/health | grep -q "healthy"; then
    print_success "Health check passed"
else
    print_error "Health check failed"
    exit 1
fi

# Test languages endpoint
if curl -s http://localhost:2358/languages | grep -q "name"; then
    print_success "Languages endpoint working"
else
    print_warning "Languages endpoint not responding properly"
fi

echo ""

# Step 6: Success message and next steps
print_step "6" "🎉 Installation complete!"
echo ""

print_success "Judge0 Spring Boot is now running!"
echo ""

echo "📍 Access Points:"
echo "   • Main API: http://localhost:2358"
echo "   • Health Check: http://localhost:2358/health"
echo "   • System Info: http://localhost:2358/system_info"
echo "   • JobRunr Dashboard: http://localhost:8081"
echo ""

echo "🧪 Quick Tests:"
echo ""

# Test with Python
echo "Test Python code execution:"
echo "curl -X POST http://localhost:2358/submissions \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"source_code\":\"print(\\\"Hello, Judge0 Spring!\\\")\",\"language_id\":71}'"
echo ""

# Test with immediate result
echo "Get immediate result (synchronous):"
echo "curl -X POST \"http://localhost:2358/submissions?wait=true\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"source_code\":\"print(\\\"Hello, World!\\\")\",\"language_id\":71}'"
echo ""

echo "📚 Documentation:"
echo "   • README.md - Complete documentation"
echo "   • INSTALLATION.md - Detailed installation guide"
echo "   • API Documentation: Compatible with Judge0 API"
echo ""

echo "🔧 Management Commands:"
if [ "$choice" = "1" ]; then
    echo "   • View logs: docker-compose logs -f judge0-api"
    echo "   • Stop services: docker-compose down"
    echo "   • Restart: docker-compose restart judge0-api"
elif [ "$choice" = "2" ]; then
    echo "   • Stop: Press Ctrl+C"
    echo "   • Restart: ./scripts/server"
    echo "   • View logs: tail -f logs/judge0-spring.log"
elif [ "$choice" = "3" ]; then
    echo "   • View logs: docker logs -f judge0-api"
    echo "   • Stop: docker stop judge0-api"
    echo "   • Restart: docker restart judge0-api"
fi

echo ""
echo "🎯 Next Steps:"
echo "   1. Test the API with the examples above"
echo "   2. Explore the JobRunr dashboard at http://localhost:8081"
echo "   3. Check out the README.md for more advanced usage"
echo "   4. Set up monitoring with: docker-compose --profile monitoring up -d"
echo ""

print_success "Happy coding! 🚀"

# Run a quick test automatically
echo "Running automatic test..."
TEST_RESULT=$(curl -s -X POST "http://localhost:2358/submissions?wait=true" \
  -H "Content-Type: application/json" \
  -d '{"source_code":"print(\"Judge0 Spring Boot is working!\")","language_id":71}')

if echo "$TEST_RESULT" | grep -q "Judge0 Spring Boot is working!"; then
    print_success "Automatic test passed! ✨"
    echo ""
    echo "Example output:"
    echo "$TEST_RESULT" | python3 -m json.tool 2>/dev/null || echo "$TEST_RESULT"
else
    print_warning "Automatic test didn't complete successfully, but the system is running."
    echo "You can test manually with the commands above."
fi

echo ""
print_success "All done! Judge0 Spring Boot is ready to use! 🎉"
