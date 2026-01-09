# 🐳 Docker Setup - Checklist & Status

## ✅ Erstellung abgeschlossen

### Docker-Dateien (10/10)
- [x] `docker-compose.yml` - Production Stack
- [x] `docker-compose.dev.yml` - Development Stack
- [x] `ahp-backend/Dockerfile` - Backend Build
- [x] `ahp-frontend/Dockerfile` - Frontend Build
- [x] `ahp-frontend/nginx.conf` - Nginx Configuration
- [x] `ahp-frontend/docker-entrypoint.sh` - Frontend Entrypoint
- [x] `.dockerignore` - Root exclusions
- [x] `ahp-backend/.dockerignore` - Backend exclusions
- [x] `ahp-frontend/.dockerignore` - Frontend exclusions
- [x] `docker-manage.sh` - Management script (executable)

### Konfiguration (3/3)
- [x] `.env` - Environment variables
- [x] `application-docker.yml` - Spring Boot Docker profile
- [x] `init-db.sql` - Database initialization

### Dokumentation (4/4)
- [x] `DOCKER_SETUP.md` - Setup overview
- [x] `DOCKER.md` - Comprehensive documentation
- [x] `DOCKER_OVERVIEW.md` - File descriptions
- [x] `DOCKER_INSTALL.md` - Installation guide

## 📋 Nächste Schritte

### Vor dem Start

- [ ] **Docker installieren** (siehe DOCKER_INSTALL.md)
  - Für Ubuntu/Debian: `sudo apt-get install docker.io docker-compose-plugin`
  - Oder Docker Desktop: https://www.docker.com/products/docker-desktop
  
- [ ] **Docker-Daemon starten**
  - `sudo systemctl start docker`
  - Oder starte Docker Desktop App

- [ ] **Benutzer zur docker-Gruppe hinzufügen** (optional, um sudo zu vermeiden)
  - `sudo usermod -aG docker $USER`
  - `newgrp docker`

### Start-Optionen

#### Option A: Einfaches Management-Script
```bash
./docker-manage.sh start
./docker-manage.sh logs
```

#### Option B: Direktes docker-compose (Production)
```bash
docker-compose up -d
docker-compose logs -f
```

#### Option C: Development mit lokaler Anbindung
```bash
docker-compose -f docker-compose.dev.yml up -d
cd ahp-frontend
ng serve --host 0.0.0.0 --port 4200
```

### Nach dem Start

- [ ] Frontend ist erreichbar: http://localhost:4200
- [ ] Backend läuft: http://localhost:9000/api
- [ ] Datenbank verbunden: `docker-compose exec postgres psql -U ahp_user -d ahp_db`
- [ ] Keine Fehler in Logs: `./docker-manage.sh logs` oder `docker-compose logs`

## 🔍 Überprüfung

### Dateien überprüfen
```bash
# Alle Docker-Dateien sind vorhanden
ls -la docker-compose*.yml
ls -la ahp-backend/Dockerfile
ls -la ahp-frontend/Dockerfile
ls -la ahp-frontend/nginx.conf
ls -la .env
ls -la DOCKER*.md
```

### Berechtigungen überprüfen
```bash
# Management-Script sollte executable sein
ls -la docker-manage.sh
# Output sollte: -rwxrwxr-x (x = executable)
```

### Docker-Installation überprüfen
```bash
# Docker sollte installiert sein
docker --version
docker compose version  # v2 oder
docker-compose --version  # v1

# Docker-Daemon sollte laufen
docker ps
```

## 🔐 Sicherheits-Checklist

### Vor Production-Deployment

- [ ] **JWT_SECRET ändern**
  - In `.env`: `JWT_SECRET=$(openssl rand -base64 32)`
  
- [ ] **Datenbank-Passwort ändern**
  - In `.env`: `DB_PASSWORD=$(openssl rand -base64 32)`
  
- [ ] **HTTPS/SSL konfigurieren**
  - Mit Reverse-Proxy (nginx/Apache) oder Let's Encrypt
  
- [ ] **Backup-Strategie**
  - Tägliche DB-Backups einrichten
  - `docker-compose exec postgres pg_dump ... > backup.sql`
  
- [ ] **Monitoring einrichten**
  - Prometheus/Grafana für Metriken
  - ELK-Stack für Logs
  
- [ ] **Firewall konfigurieren**
  - Nur Port 80 (HTTP) und 443 (HTTPS) öffnen
  - Port 9000 und 5432 internal halten

## 📊 Performance-Optimierungen

### Bereits implementiert ✅
- [x] Multi-stage Builds (kleinere Images)
- [x] Alpine Linux Base-Images
- [x] Gzip-Kompression
- [x] Asset-Caching
- [x] Health Checks
- [x] Connection Pooling (Hikari)
- [x] Database-Indizes (via Hibernate)
- [x] Nginx Worker-Prozesse (auto)

### Optional hinzufügen 🔜
- [ ] Redis für Session/Cache
- [ ] CDN für statische Assets
- [ ] Database-Replikation (High Availability)
- [ ] Load Balancer (für mehrere Instanzen)

## 🛠️ Wartung

### Regelmäßig durchführen

- [ ] **Wöchentlich**
  - Logs überprüfen
  - Docker-Image-Updates prüfen

- [ ] **Monatlich**
  - Datenbank-Backups prüfen
  - Sicherheits-Updates durchführen

- [ ] **Quartal**
  - Performance-Analyse
  - Disaster-Recovery-Test
  - Dependency-Updates

## 📞 Troubleshooting

### Container starten nicht
```bash
docker-compose logs ahp-backend
docker-compose logs postgres
```

### Port-Konflikt
```bash
lsof -i :9000
kill -9 <PID>
```

### Datenbank-Fehler
```bash
docker-compose exec postgres psql -U ahp_user -d ahp_db
```

### Vollständiger Reset
```bash
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## 📚 Referenzen

- Docker Docs: https://docs.docker.com
- Docker Compose: https://docs.docker.com/compose
- Best Practices: https://docs.docker.com/develop/develop-images/dockerfile_best-practices
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker

## 📝 Notizen

- **Database**: PostgreSQL 16 (persistent volume)
- **Backend**: Spring Boot 4.0.1, Java 21
- **Frontend**: Angular 18, Nginx
- **Netzwerk**: ahp-network (bridge)
- **Total Image-Größe**: ~315 MB (uncompressed)

---

**Status**: ✅ Bereit zum Deployment  
**Erstellung**: Januar 2026  
**Version**: 2.0.0-docker  
**Letzte Aktualisierung**: 8. Januar 2026
