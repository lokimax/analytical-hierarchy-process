# 🐳 AHP Application - Docker Setup Complete

Dein Projekt ist vollständig dockerisiert und produktionsbereit!

## ✅ Was wurde erstellt

### Docker-Dateien (10 Dateien)

| Datei | Zweck |
|-------|-------|
| `docker-compose.yml` | Production Stack (Backend + Frontend + Database) |
| `docker-compose.dev.yml` | Development Stack (Backend + Database, Frontend lokal) |
| `ahp-backend/Dockerfile` | Multi-stage Build für Spring Boot |
| `ahp-frontend/Dockerfile` | Multi-stage Build für Angular + Nginx |
| `ahp-frontend/nginx.conf` | Nginx-Konfiguration mit API-Proxy |
| `ahp-frontend/docker-entrypoint.sh` | Frontend-Startup-Script |
| `.dockerignore` | Root-Level Ausschlüsse |
| `ahp-backend/.dockerignore` | Backend-Ausschlüsse |
| `ahp-frontend/.dockerignore` | Frontend-Ausschlüsse |
| `docker-manage.sh` | Hilfsskript für einfache Befehle |

### Konfigurationsdateien (3 Dateien)

| Datei | Zweck |
|-------|-------|
| `.env` | Umgebungsvariablen |
| `application-docker.yml` | Spring Boot Docker-Profil |
| `init-db.sql` | Database-Initialisierung (optional) |

### Dokumentation (3 Dateien)

| Datei | Zweck |
|-------|-------|
| `DOCKER.md` | Ausführliche Docker-Dokumentation |
| `DOCKER_OVERVIEW.md` | Übersicht aller Docker-Dateien |
| `DOCKER_INSTALL.md` | Docker Installation Guide |

## 🚀 Quick Start

### Schritt 1: Docker installieren
```bash
# Folge DOCKER_INSTALL.md für dein Betriebssystem
```

### Schritt 2: Application starten

**Option A: Production-Stack (alles in Docker)**
```bash
docker-compose up -d
# oder
docker compose up -d    # Docker Desktop
```

**Option B: Development-Stack (Backend in Docker, Frontend lokal)**
```bash
docker-compose -f docker-compose.dev.yml up -d
cd ahp-frontend
ng serve --host 0.0.0.0 --port 4200
```

**Option C: Management-Script (am einfachsten)**
```bash
./docker-manage.sh start
./docker-manage.sh logs
```

### Schritt 3: Zugriff
- **Frontend**: http://localhost:4200 oder http://localhost
- **Backend**: http://localhost:9000/api
- **Database**: localhost:5432

## 📊 Architecture

```
┌─────────────────────────────────────────────┐
│         Docker Compose Network              │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Frontend │  │ Backend  │  │ Database │  │
│  │ (Nginx)  │  │(Spring)  │  │(Postgres)│  │
│  │ Port 80  │  │Port 9000 │  │Port 5432 │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
│       │             │             │        │
│       └─────────────┼─────────────┘        │
│                     │                      │
│           ahp-network (bridge)             │
│                                             │
└─────────────────────────────────────────────┘
         ↓
    localhost
```

## 🔧 Kommandos

### Mit docker-compose CLI

```bash
# Start im Hintergrund
docker-compose up -d

# Logs anzeigen
docker-compose logs -f

# Services stoppen
docker-compose down

# Alles löschen (inklusive Daten!)
docker-compose down -v

# Spezifischen Service neu bauen
docker-compose build ahp-backend
```

### Mit Management-Script

```bash
./docker-manage.sh start            # Services starten
./docker-manage.sh stop             # Services stoppen
./docker-manage.sh restart          # Services neustarten
./docker-manage.sh logs             # Alle Logs
./docker-manage.sh logs-backend     # Nur Backend-Logs
./docker-manage.sh logs-frontend    # Nur Frontend-Logs
./docker-manage.sh logs-db          # Nur DB-Logs
./docker-manage.sh build            # Neu bauen
./docker-manage.sh status           # Service-Status
./docker-manage.sh shell-backend    # In Backend-Container
./docker-manage.sh shell-db         # In DB-Container
./docker-manage.sh clean            # Alles löschen
```

## 🔐 Sicherheit

Für Production-Einsatz:

- [ ] **JWT_SECRET in .env ändern**
  ```env
  JWT_SECRET=$(openssl rand -base64 32)
  ```

- [ ] **Starke DB-Passwords verwenden**
  ```env
  DB_PASSWORD=$(openssl rand -base64 32)
  ```

- [ ] **HTTPS/SSL konfigurieren** (Reverse-Proxy empfohlen)

- [ ] **Backups automatisieren**
  ```bash
  docker-compose exec postgres pg_dump -U ahp_user ahp_db > backup.sql
  ```

- [ ] **Logging einrichten** (E.g., ELK Stack)

- [ ] **Monitoring** (E.g., Prometheus + Grafana)

## 📈 Performance

### Images-Größe (Ungefähr)

| Image | Größe |
|-------|-------|
| postgres:16-alpine | ~75 MB |
| eclipse-temurin:21-jre | ~200 MB |
| nginx:alpine | ~40 MB |
| **Total** | **~315 MB** |

### Optimierungen bereits implementiert

✅ Multi-stage Builds (kleinere Runtime-Images)
✅ Alpine Base-Images (minimal)
✅ Gzip-Kompression (Frontend)
✅ Asset-Caching (1 Jahr TTL)
✅ Health Checks (automatische Neustarts)
✅ Connection Pooling (Hikari)
✅ Nginx Worker Processes (auto)

## 🐛 Troubleshooting

### Container starten nicht

```bash
# Logs überprüfen
docker-compose logs

# spezifischen Service debuggen
docker-compose logs ahp-backend

# Neubau mit verbose Output
docker-compose build --verbose --no-cache
```

### Port bereits in Benutzung

```bash
# Wer nimmt Port 9000?
lsof -i :9000

# Process beenden
kill -9 <PID>

# Oder Port in docker-compose.yml ändern:
# ports:
#   - "9001:9000"   # Host:Container
```

### Datenbankverbindung schlägt fehl

```bash
# In Database-Container gehen
docker-compose exec postgres psql -U ahp_user -d ahp_db

# Connection-String prüfen (sollte sein):
# jdbc:postgresql://postgres:5432/ahp_db
#                   ^^^^^^^
#        Container-Hostname!
```

## 📚 Weitere Dokumentation

- `DOCKER.md` - Ausführliche Dokumentation
- `DOCKER_OVERVIEW.md` - Datei-Übersicht
- `DOCKER_INSTALL.md` - Installation Guide

## 🎯 Nächste Schritte

1. **Docker installieren** → siehe DOCKER_INSTALL.md
2. **Application starten** → `docker-compose up -d`
3. **Tests durchführen** → siehe Testlogs
4. **Deploy-Strategie planen** → siehe Production-Dokumentation

## 🔗 Useful Links

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)

---

**Status**: ✅ Docker Setup abgeschlossen  
**Datum**: Januar 2026  
**Version**: 2.0.0-docker
