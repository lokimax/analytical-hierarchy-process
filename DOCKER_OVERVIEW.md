# Docker & Docker Compose Setup - Übersicht

Dieses Projekt ist vollständig für Docker containerisiert. Im Folgenden findest du eine Übersicht aller Docker-bezogenen Dateien und deren Zweck.

## 📁 Docker-Dateien

### Produktionsumgebung (docker-compose.yml)

Vollständiger Stack mit allen 3 Services:

```
docker-compose.yml          ← Hauptkonfiguration für Production
├── Service: postgres       ← PostgreSQL 16 (Port 5432)
├── Service: ahp-backend    ← Spring Boot (Port 9000)
└── Service: ahp-frontend   ← Angular + Nginx (Port 80, 4200)
```

**Starten:**
```bash
docker-compose up -d
docker-compose logs -f
```

### Entwicklungsumgebung (docker-compose.dev.yml)

Vereinfachter Stack ohne Frontend-Container:

```
docker-compose.dev.yml      ← Für lokale ng serve-Entwicklung
├── Service: postgres       ← PostgreSQL 16
└── Service: ahp-backend    ← Spring Boot
    (Frontend läuft lokal auf Port 4200)
```

**Starten:**
```bash
docker-compose -f docker-compose.dev.yml up -d
ng serve --host 0.0.0.0 --port 4200
```

### Backend Dockerfile

```
ahp-backend/Dockerfile     ← Multi-stage build für Spring Boot
├── Stage 1 (builder)      ← Maven kompiliert die Anwendung
└── Stage 2 (runtime)      ← Nur JRE mit dem JAR
```

**Basis-Images:**
- Builder: `eclipse-temurin:21-jdk-jammy` (JDK 21)
- Runtime: `eclipse-temurin:21-jre-jammy` (JRE 21)

### Frontend Dockerfile

```
ahp-frontend/Dockerfile    ← Multi-stage build für Angular
├── Stage 1 (builder)      ← Node 18 + npm + ng build
└── Stage 2 (runtime)      ← Nginx Alpine mit produziertem Bundle
```

**Basis-Images:**
- Builder: `node:18.18.0-alpine` (Node 18.18.0)
- Runtime: `nginx:alpine` (neuestes Nginx)

### Konfigurationsdateien

```
.env                        ← Umgebungsvariablen (Git-ignoriert)
├── DB_NAME, DB_USER, DB_PASSWORD
├── SPRING_PROFILE=docker
├── JWT_SECRET
└── API_URL, NODE_ENV

.dockerignore               ← Dateien, die in Docker-Images ausgeschlossen werden
├── .git/, .gitignore
├── node_modules/, target/
├── *.log, *.md
└── etc.

ahp-backend/.dockerignore   ← Backend-spezifische Ausschlüsse
ahp-frontend/.dockerignore  ← Frontend-spezifische Ausschlüsse
```

### Nginx-Konfiguration

```
ahp-frontend/nginx.conf     ← Nginx-Konfiguration für Production
├── Port 80 Listener
├── API Proxy zu Backend (http://ahp-backend:9000)
├── SPA-Routing (try_files $uri $uri/ /index.html)
├── Security Headers (CSP, X-Frame-Options, etc.)
├── Gzip Kompression
├── Asset Caching (1 Jahr)
└── Health Check Endpoint (/health)

ahp-frontend/docker-entrypoint.sh  ← Startup-Script
└── Ersetzt API_URL in Bundles + startet Nginx
```

### Spring Boot Docker-Profil

```
ahp-backend/src/main/resources/application-docker.yml
├── PostgreSQL-Verbindung
├── CORS-Einstellungen
├── Logging für Docker
├── Hibernate DDL Auto
└── Connection Pool (Hikari)
```

### Datenbank-Initialisierung

```
init-db.sql                 ← Optional: DB-Initialisierungsskript
                            ← (Hibernate erstellt Tabellen automatisch)
```

### Management-Script

```
docker-manage.sh            ← Hilfsskript für einfache Befehle
├── ./docker-manage.sh start         → Services starten
├── ./docker-manage.sh stop          → Services stoppen
├── ./docker-manage.sh logs          → Logs anzeigen
├── ./docker-manage.sh build         → Neu bauen
├── ./docker-manage.sh shell-backend → In Backend-Container
└── ./docker-manage.sh shell-db      → In DB-Container
```

### Dokumentation

```
DOCKER.md                   ← Ausführliche Docker-Dokumentation
DOCKER_OVERVIEW.md          ← Diese Datei
```

## 🚀 Quick Commands

### Production-Stack starten
```bash
docker-compose up -d
# oder mit docker compose (Docker Desktop)
docker compose up -d
```

### Entwicklungs-Stack starten
```bash
docker-compose -f docker-compose.dev.yml up -d
cd ahp-frontend && ng serve --host 0.0.0.0 --port 4200
```

### Management-Script verwenden
```bash
./docker-manage.sh start          # Services starten
./docker-manage.sh logs           # Alle Logs
./docker-manage.sh logs-backend   # Nur Backend-Logs
./docker-manage.sh shell-backend  # In Backend-Container
./docker-manage.sh shell-db       # In Datenbank-Container
./docker-manage.sh status         # Service-Status
./docker-manage.sh clean          # Alles löschen
```

## 📊 Port-Zuordnung

| Service | Port | URL | Beschreibung |
|---------|------|-----|-------------|
| Frontend | 80 | http://localhost | Production Frontend (Nginx) |
| Frontend | 4200 | http://localhost:4200 | Dev Frontend (ng serve) |
| Backend | 9000 | http://localhost:9000 | Spring Boot API |
| Database | 5432 | localhost:5432 | PostgreSQL |

## 🔧 Netzwerk

```
docker-compose.yml nutzt:
- Network: ahp-network (bridge)
- Services können sich über Hostnamen erreichen:
  - Backend: http://ahp-backend:9000
  - Database: postgres:5432
  - Frontend: http://ahp-frontend

docker-compose.dev.yml nutzt:
- Network: ahp-network-dev (bridge)
- Separate Volumes für dev-Datenbank
```

## 💾 Volumes

### Production (docker-compose.yml)
```
postgres_data               ← PostgreSQL-Daten (persistent)
                            ← Wird nicht gelöscht bei `docker-compose down`
```

### Development (docker-compose.dev.yml)
```
postgres_data_dev          ← Dev-DB (separate Daten)
                            ← Kann einfach mit `down -v` gelöscht werden
```

## 🔐 Sicherheit

### Docker-Ebene
- Multi-stage Builds (kleinere Images)
- Non-root User (optimal)
- Health Checks für alle Services
- Keine hardcodierten Secrets (use .env)

### Nginx-Ebene (Frontend)
- Security Headers aktiviert
- CSP (Content Security Policy)
- X-Frame-Options (Clickjacking-Schutz)
- Gzip-Kompression
- TLS/HTTPS-ready (mit Reverse-Proxy)

### Spring Boot-Ebene (Backend)
- CORS-Einstellungen konfigurierbar
- JWT Authentication
- Password Encoding (BCrypt)
- Environment-variablenbasierte Secrets

## 📝 Umgebungsvariablen

### .env Datei (im Root-Verzeichnis)

```env
# Database
DB_NAME=ahp_db
DB_USER=ahp_user
DB_PASSWORD=ahp_password

# Spring Boot
DDL_AUTO=update              # Hibernate DDL strategy
SPRING_PROFILE=docker        # Aktives Spring-Profil
LOG_LEVEL=INFO               # Root-Log-Level

# JWT
JWT_SECRET=your-secret-key   # WICHTIG: In Production ändern!

# Frontend
API_URL=http://localhost:9000
NODE_ENV=production
```

## 🛠️ Troubleshooting

### Services starten nicht
```bash
# Logs überprüfen
docker-compose logs

# Docker-Daemon prüfen
docker ps
docker ps -a

# Ports überprüfen
netstat -tulpn | grep 9000
netstat -tulpn | grep 5432
```

### Datenbankverbindung fehlgeschlagen
```bash
# Postgres-Health überprüfen
docker-compose exec postgres pg_isready

# Postgres-Logs
docker-compose logs postgres

# In Container gehen
docker-compose exec postgres psql -U ahp_user -d ahp_db
```

### Frontend blank page
```bash
# Nginx-Logs
docker-compose logs ahp-frontend

# Browser Cache löschen (Ctrl+Shift+Delete)

# Container neustarten
docker-compose restart ahp-frontend
```

### Build-Fehler
```bash
# Cache löschen
docker-compose build --no-cache

# Vollständig neubau
docker-compose down -v
docker-compose up --build
```

## 📚 Weitere Ressourcen

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Dockerfile Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)

---

**Erstellt**: Januar 2026  
**Version**: 2.0.0
