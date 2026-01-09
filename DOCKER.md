# AHP Application - Docker Compose Setup

Vollständiger Docker-basierter Setup der AHP (Analytical Hierarchy Process) Anwendung mit Spring Boot Backend, Angular Frontend und PostgreSQL Datenbank.

## Voraussetzungen

- Docker & Docker Compose (Version 3.8+)
- Mindestens 4GB RAM für Docker
- Ports 80, 4200, 9000, 5432 müssen verfügbar sein

## Quick Start

### 1. Anwendung starten

```bash
# Mit Standard-Einstellungen starten
docker-compose up -d

# Mit Logs ansehen
docker-compose up

# Mit Rebuild
docker-compose up --build
```

### 2. Zugriff auf die Anwendung

- **Frontend**: http://localhost:4200 oder http://localhost
- **Backend API**: http://localhost:9000/api
- **Datenbank**: localhost:5432

### 3. Anwendung stoppen

```bash
docker-compose down

# Mit Datenlöschung
docker-compose down -v
```

## Konfiguration

### Umgebungsvariablen (.env Datei)

Bearbeite `.env` im Root-Verzeichnis:

```env
# Database
DB_NAME=ahp_db
DB_USER=ahp_user
DB_PASSWORD=ahp_password

# Spring Boot
DDL_AUTO=update              # update, validate, create-drop
SPRING_PROFILE=docker
LOG_LEVEL=INFO               # DEBUG, INFO, WARN, ERROR

# JWT Security
JWT_SECRET=change-me-in-production

# Frontend
API_URL=http://localhost:9000
NODE_ENV=production
```

### Hibernate DDL Optionen

- `create-drop`: Tabellen löschen und neu erstellen (Entwicklung)
- `update`: Änderungen aktualisieren (Default in Docker)
- `validate`: Nur validieren, keine Änderungen (Production)
- `none`: Keine Änderungen

## Services

### PostgreSQL (postgres)
- Port: 5432
- Datenbank: ahp_db
- Benutzer: ahp_user
- Passwort: ahp_password
- Volume: `postgres_data` (persistent)

### Spring Boot Backend (ahp-backend)
- Port: 9000
- Profil: docker
- Datenbank: PostgreSQL
- Health Check: `/api/public/health`

### Angular Frontend (ahp-frontend)
- Port: 80 (nginx)
- Alternative Port: 4200
- API Proxy: `/api/` → Backend
- Static Assets: Cached für 1 Jahr

## Häufige Befehle

```bash
# Status aller Services
docker-compose ps

# Logs eines spezifischen Service
docker-compose logs -f ahp-backend
docker-compose logs -f ahp-frontend
docker-compose logs -f postgres

# In einen Container gehen
docker-compose exec postgres psql -U ahp_user -d ahp_db
docker-compose exec ahp-backend bash

# Neubau eines spezifischen Service
docker-compose build ahp-backend
docker-compose build ahp-frontend

# Einzelnen Service starten/stoppen
docker-compose up -d ahp-backend
docker-compose stop ahp-frontend

# Volles Reset (Vorsicht!)
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## Datenbank-Zugriff

### Mit PostgreSQL Client

```bash
# In Container verbinden
docker-compose exec postgres psql -U ahp_user -d ahp_db

# SQL-Befehle
\dt                    # Alle Tabellen zeigen
\l                     # Alle Datenbanken
SELECT VERSION();      # PostgreSQL-Version
\q                     # Beenden
```

### Mit pgAdmin (Optional)

Füge zu docker-compose.yml hinzu:

```yaml
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: ahp-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@example.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres
    networks:
      - ahp-network
```

## Troubleshooting

### Port bereits in Benutzung

```bash
# Port freigeben
lsof -i :9000    # Backend
lsof -i :4200    # Frontend
lsof -i :5432    # Database

# Process beenden
kill -9 <PID>
```

### Datenbank-Verbindung fehlgeschlagen

```bash
# Postgres-Status überprüfen
docker-compose logs postgres

# Health Check
docker-compose exec postgres pg_isready

# Neustart erzwingen
docker-compose restart postgres
```

### Frontend zeigt blank page

```bash
# Nginx-Logs
docker-compose logs -f ahp-frontend

# Container neustarten
docker-compose restart ahp-frontend

# Cache löschen (Browser: Ctrl+Shift+Delete)
```

### Build-Fehler

```bash
# Cache löschen und neu bauen
docker-compose build --no-cache

# Image-Cleanup
docker image prune -a
```

## Performance-Optimierung

### Für Produktionsumgebung

1. **JWT Secret ändern** in `.env`
2. **DDL_AUTO** auf `validate` setzen
3. **LOG_LEVEL** auf `WARN` setzen
4. **PostgreSQL Backup** einrichten:

```bash
# Backup erstellen
docker-compose exec postgres pg_dump -U ahp_user ahp_db > backup.sql

# Backup einspielen
docker-compose exec -T postgres psql -U ahp_user ahp_db < backup.sql
```

5. **Security Headers** im Nginx aktiviert (siehe nginx.conf)
6. **CORS** für spezifische Domänen konfigurieren

## Sicherheitshinweise

⚠️ **Für Produktionsumgebung:**

- [ ] JWT_SECRET ändern (zufälliger, langer String)
- [ ] Starke DB-Passwörter verwenden
- [ ] PostgreSQL nur internal exponieren (nicht Port 5432 öffnen)
- [ ] HTTPS/SSL konfigurieren
- [ ] Backups automatisieren
- [ ] Firewall-Regeln implementieren
- [ ] Regular Security Updates durchführen

## Entwicklung

### Hot-Reload für Backend

Backend-Änderungen erfordern Rebuild:

```bash
docker-compose build ahp-backend
docker-compose up -d ahp-backend
```

### Hot-Reload für Frontend

Für Live-Reload während Entwicklung:

```bash
# Im ahp-frontend Directory
npm install
ng serve --host 0.0.0.0 --port 4200
```

Verbinde dann mit `http://localhost:4200`

## CI/CD Integration

Beispiel für GitHub Actions in `.github/workflows/docker.yml`:

```yaml
name: Build Docker Images
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: docker/setup-buildx-action@v1
      - uses: docker/build-push-action@v2
        with:
          context: .
          file: ./ahp-backend/Dockerfile
          push: true
          tags: myregistry/ahp-backend:latest
```

## Support & Logs

```bash
# Alle Logs
docker-compose logs --all

# Nur Fehler
docker-compose logs | grep ERROR

# Logs folgen (tail -f)
docker-compose logs -f --tail=100
```

---

**Erstellt**: Januar 2026  
**Version**: 2.0.0
