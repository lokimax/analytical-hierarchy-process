#!/bin/bash
# AHP Docker/Podman management helper
set -euo pipefail

COMPOSE_FILE="docker-compose.dev.yml"
COMPOSE_CMD=""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_success() { echo -e "${GREEN}✓ $1${NC}"; }
print_error()   { echo -e "${RED}✗ $1${NC}"; }
print_info()    { echo -e "${BLUE}ℹ $1${NC}"; }
print_header()  { echo -e "${BLUE}==================== $1 ====================${NC}"; }

check_compose_tool() {
    if command -v podman-compose >/dev/null 2>&1; then
        COMPOSE_CMD="podman-compose"
    elif command -v docker >/dev/null 2>&1; then
        COMPOSE_CMD="docker compose"
    else
        print_error "No container tool found. Install Podman or Docker."
        exit 1
    fi
}

start() {
    print_header "Starting AHP"
    $COMPOSE_CMD -f "$COMPOSE_FILE" up -d
    print_success "Services started"
    print_info "Frontend/Backend: http://localhost:8080"
    print_info "MailHog:          http://localhost:8025"
    print_info "Postgres:         localhost:5432"
}

stop() {
    print_header "Stopping AHP"
    $COMPOSE_CMD -f "$COMPOSE_FILE" down
    print_success "Services stopped"
}

build() {
    print_header "Building Maven & Images"
    mvn -q -DskipTests clean package
    $COMPOSE_CMD -f "$COMPOSE_FILE" build
    print_success "Build complete"
}

smooth() {
    print_header "Smooth Startup"
    $COMPOSE_CMD -f "$COMPOSE_FILE" down 2>/dev/null || true
    if [[ "${1:-}" == "--build" ]]; then
        build
    fi
    start
    sleep 3
    $COMPOSE_CMD -f "$COMPOSE_FILE" ps
    print_success "Ready"
}

logs() {
    if [[ -n "${1:-}" ]]; then
        $COMPOSE_CMD -f "$COMPOSE_FILE" logs -f "$1"
    else
        $COMPOSE_CMD -f "$COMPOSE_FILE" logs -f
    fi
}

status() {
    print_header "Status"
    $COMPOSE_CMD -f "$COMPOSE_FILE" ps
}

clean() {
    print_header "Clean"
    $COMPOSE_CMD -f "$COMPOSE_FILE" down -v
    print_success "Removed containers and volumes"
}

shell_backend() { $COMPOSE_CMD -f "$COMPOSE_FILE" exec ahp-backend /bin/sh; }
shell_db()      { $COMPOSE_CMD -f "$COMPOSE_FILE" exec postgres psql -U ahp_user -d ahp_db; }

usage() {
    cat <<EOF
AHP Docker Management

Usage: ./docker-manage.sh [command]

Commands:
  smooth [--build]  Smooth startup (stop, optional build, start)
  start             Start services
  stop              Stop services
  build             Build Maven and images
  logs [service]    View logs (all or one service)
  status            Show container status
  clean             Remove containers and volumes
  shell-backend     Shell into backend container
  shell-db          psql inside postgres container
EOF
}

check_compose_tool
case "${1:-}" in
  smooth) smooth "${2:-}" ;;
  start) start ;;
  stop) stop ;;
  build) build ;;
  logs) logs "${2:-}" ;;
  status) status ;;
  clean) clean ;;
  shell-backend) shell_backend ;;
  shell-db) shell_db ;;
  *) usage ;;
esac
