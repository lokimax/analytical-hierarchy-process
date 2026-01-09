# 🚀 Build & Deploy Guide

## Ein Befehl für alles

```bash
# Im Root-Verzeichnis
mvn clean package -DskipTests

# Das macht automatisch:
# 1. ahp-frontend: Node 18.19.1 + npm 10.2.3 installieren → npm install → ng build --production
# 2. Kopiert dist/ahp-frontend/* → ahp-backend/src/main/resources/static/
# 3. ahp-core: Maven compile & package
# 4. ahp-backend: Maven package + Jib dockerBuild → Container Image ahp-backend:latest (264 MB)
#
# Build-Zeit: ~45-50 Sekunden
# Container: eclipse-temurin:21-jre-alpine
```

## Container starten

```bash
podman-compose -f docker-compose.dev.yml up -d

# Oder mit Docker:
docker compose -f docker-compose.dev.yml up -d
```

## Zugriff

- **Frontend & Backend**: http://localhost:8080
- **Backend API**: http://localhost:8080/api
- **MailHog (Email Testing)**: http://localhost:8025

## Services

- **postgres**: PostgreSQL 16 auf Port 5432
- **mailhog**: SMTP 1025, Web UI 8025
- **ahp-backend**: Spring Boot auf Port 8080 (liefert Frontend aus)

## Schnellstart

```bash
# 1. Alles bauen (Frontend + Backend + Container)
mvn clean package

# 2. Container starten
podman-compose -f docker-compose.dev.yml up -d

# 3. Logs anschauen
podman logs -f ahp-backend-dev

# 4. Browser öffnen
xdg-open http://localhost:8080
```

## Entwicklung

Für lokale Entwicklung ohne Container:

```bash
# PostgreSQL + MailHog im Container
podman-compose -f docker-compose.dev.yml up -d postgres mailhog

# Backend lokal
cd ahp-backend
mvn spring-boot:run

# Frontend lokal (in neuem Terminal)
cd ahp-frontend
npm start
# → Frontend auf http://localhost:4200 mit HMR
```

## Troubleshooting

### Container neu bauen
```bash
mvn clean package
podman-compose -f docker-compose.dev.yml up -d --force-recreate
```

### Nur Backend neu bauen
```bash
mvn clean package -pl ahp-backend -am
```

### Nur Frontend neu bauen
```bash
mvn clean package -pl ahp-frontend
```

### Image prüfen
```bash
podman images | grep ahp-backend
```

### Container stoppen
```bash
podman-compose -f docker-compose.dev.yml down
```
