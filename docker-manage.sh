#!/bin/bash

# Script zum Verwalten der Docker Compose Services
# Verwendung: ./docker-manage.sh [start|stop|restart|logs|build|clean]

set -e

PROJECT_NAME="ahp"
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funktionen
usage() {
    echo "Verwendung: $0 [BEFEHL]"
    echo ""
    echo "Befehle:"
    echo "  start          - Services starten (im Hintergrund)"
    echo "  stop           - Services stoppen"
    echo "  restart        - Services neustarten"
    echo "  logs           - Logs aller Services anzeigen"
    echo "  logs-backend   - Nur Backend-Logs"
    echo "  logs-frontend  - Nur Frontend-Logs"
    echo "  logs-db        - Nur Datenbank-Logs"
    echo "  build          - Services neu bauen"
    echo "  status         - Status aller Services"
    echo "  clean          - Services & Volumes löschen"
    echo "  shell-backend  - In Backend-Container gehen"
    echo "  shell-db       - In DB-Container gehen"
    echo ""
}

start() {
    echo -e "${YELLOW}Starting AHP Services...${NC}"
    docker-compose -f $COMPOSE_FILE up -d
    echo -e "${GREEN}✓ Services gestartet${NC}"
    echo ""
    echo "Zugriffe:"
    echo "  Frontend: http://localhost:4200"
    echo "  Backend:  http://localhost:9000/api"
    echo "  Postgres: localhost:5432"
}

stop() {
    echo -e "${YELLOW}Stopping AHP Services...${NC}"
    docker-compose -f $COMPOSE_FILE down
    echo -e "${GREEN}✓ Services gestoppt${NC}"
}

restart() {
    echo -e "${YELLOW}Restarting AHP Services...${NC}"
    docker-compose -f $COMPOSE_FILE restart
    echo -e "${GREEN}✓ Services neu gestartet${NC}"
}

logs() {
    docker-compose -f $COMPOSE_FILE logs -f --tail=100
}

logs_service() {
    docker-compose -f $COMPOSE_FILE logs -f --tail=50 "$1"
}

build() {
    echo -e "${YELLOW}Building Services...${NC}"
    docker-compose -f $COMPOSE_FILE build --no-cache
    echo -e "${GREEN}✓ Build abgeschlossen${NC}"
}

status() {
    echo -e "${YELLOW}Service Status:${NC}"
    docker-compose -f $COMPOSE_FILE ps
}

clean() {
    echo -e "${RED}WARNING: This will delete all containers and volumes!${NC}"
    read -p "Wirklich löschen? (ja/nein): " -r
    echo ""
    if [[ $REPLY =~ ^[Jj][Aa]$ ]]; then
        docker-compose -f $COMPOSE_FILE down -v
        echo -e "${GREEN}✓ Cleanup abgeschlossen${NC}"
    else
        echo "Abgebrochen"
    fi
}

shell_backend() {
    docker-compose -f $COMPOSE_FILE exec ahp-backend bash
}

shell_db() {
    docker-compose -f $COMPOSE_FILE exec postgres psql -U ahp_user -d ahp_db
}

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null && ! command -v docker compose &> /dev/null; then
    echo -e "${RED}Error: docker-compose is not installed${NC}"
    exit 1
fi

# Check if .env file exists
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Error: $ENV_FILE not found${NC}"
    exit 1
fi

# Main command handling
case "${1:-help}" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs
        ;;
    logs-backend)
        logs_service "ahp-backend"
        ;;
    logs-frontend)
        logs_service "ahp-frontend"
        ;;
    logs-db)
        logs_service "postgres"
        ;;
    build)
        build
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    shell-backend)
        shell_backend
        ;;
    shell-db)
        shell_db
        ;;
    *)
        usage
        ;;
esac
