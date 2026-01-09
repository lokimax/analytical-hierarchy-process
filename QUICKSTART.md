# 🚀 Quick Start Guide

## Einfachster Start

```bash
# 1. Alles bauen (Frontend + Backend + Container)
mvn clean package -DskipTests

# 2. Services starten
podman-compose -f docker-compose.dev.yml up -d

# 3. Browser öffnen
xdg-open http://localhost:8080
```

**Build-Zeit**: ~45-50 Sekunden  
**Container-Image**: 264 MB (eclipse-temurin:21-jre-alpine)

## Zugriffspunkte

Nach dem Start sind diese URLs verfügbar:

- **Application**: http://localhost:8080
- **Backend API**: http://localhost:8080/api
- **MailHog (Email Testing)**: http://localhost:8025
- **PostgreSQL**: localhost:5432
  - User: `ahp_user`
  - Password: `ahp_password`
  - Database: `ahp_db`

## Container Management

```bash
# Services stoppen
podman-compose -f docker-compose.dev.yml down

# Services neu starten
podman-compose -f docker-compose.dev.yml restart

# Logs anschauen
podman logs -f ahp-backend-dev

# Status prüfen
podman ps
```

## Nur Backend neu bauen

```bash
# Nur Backend + Core Module
mvn clean package -DskipTests -pl ahp-backend -am

# Container neu erstellen
podman stop ahp-backend-dev && podman rm ahp-backend-dev
podman-compose -f docker-compose.dev.yml up -d ahp-backend
```

## Troubleshooting

### Ports bereits belegt

```bash
# Finde Prozess auf Port 8080
sudo lsof -i :8080

# Stoppe alle Container
podman-compose -f docker-compose.dev.yml down
```

### Container startet nicht

```bash
# Logs checken
podman logs ahp-backend-dev

# Komplett neu bauen
mvn clean package -DskipTests
podman-compose -f docker-compose.dev.yml up -d --force-recreate
```

### Frontend zeigt 403 Forbidden

```bash
# Prüfe ob statische Dateien kopiert wurden
ls -lh ahp-backend/src/main/resources/static/

# Falls leer: Vollständiger Build nötig
mvn clean package -DskipTests
```

## Email-Aktivierung testen

1. Registriere Benutzer auf http://localhost:8080/register
2. Öffne MailHog auf http://localhost:8025
3. Klicke auf Aktivierungs-Link in Email
4. Login auf http://localhost:8080/login

## Entwicklung ohne Container

Für lokale Entwicklung mit Hot-Reload siehe [DEV_START.md](DEV_START.md)
