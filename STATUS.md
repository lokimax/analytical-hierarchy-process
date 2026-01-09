# ✅ AHP System Status

**Letzte Aktualisierung**: 2026-01-09 21:00

## 🎯 Aktueller Status: **PRODUCTION READY**

### 🏗️ Build-System
- **Maven Multi-Module**: ✅ ahp-frontend → ahp-core → ahp-backend
- **Frontend-Maven-Plugin**: ✅ Node 18.19.1 + npm 10.2.3 (eirslett)
- **Jib Maven Plugin**: ✅ Container-Build ohne Dockerfile
- **Build-Zeit**: ~45-50 Sekunden (vollständig)
- **Container-Image**: 264 MB (eclipse-temurin:21-jre-alpine)
- **Output-Pfad**: dist/ahp-frontend → ahp-backend/src/main/resources/static

### ✅ Monolithic Application
- **Status**: ✅ Running
- **Port**: 8080 (Backend serviert Frontend)
- **URL**: http://localhost:8080
- **Backend API**: http://localhost:8080/api
- **Container**: ahp-backend:latest (Jib-built)
- **Features**:
  - ✅ Email-basierte Benutzer-Aktivierung
  - ✅ JWT Authentication
  - ✅ REST API für AHP-Analysen
  - ✅ Token-System (32-Byte, 24h Gültigkeit)
  - ✅ Angular 18 Production Build embedded
  - ✅ Statische Assets (603 KB initial bundle, 127 KB gzipped)

### ✅ PostgreSQL
- **Status**: ✅ Running (Container)
- **Port**: 5432
- **Container**: ahp-postgres-dev
- **Database**: ahp_db
- **User**: ahp_user
- **Password**: ahp_password
- **Health**: Healthy

### ✅ MailHog (Email Testing)
- **Status**: ✅ Running (Container)
- **SMTP Port**: 1025
- **Web UI Port**: 8025
- **URL**: http://localhost:8025
- **Container**: ahp-mailhog-dev

## 🚀 Schnellstart

```bash
# 1. Alles bauen (Frontend + Backend + Container)
mvn clean package -DskipTests

# 2. Container starten
podman-compose -f docker-compose.dev.yml up -d

# 3. Browser öffnen
xdg-open http://localhost:8080
```

## 🧪 Features testen

### Email-Aktivierung testen
1. Gehe zu http://localhost:8080/register
2. Fülle Registrierungsformular aus
3. Öffne MailHog: http://localhost:8025
4. Finde die Aktivierungs-Email
5. Klicke auf Aktivierungs-Link
6. Account ist aktiviert → Login möglich

### API testen
```bash
# Health Check
curl http://localhost:8080/actuator/health

# Frontend
curl -I http://localhost:8080/
# Erwartete Antwort: HTTP/1.1 200
```

## 🛠️ Technische Details

### Aktueller Build-Prozess

```bash
mvn clean package -DskipTests
```

**Was passiert:**

1. **ahp-frontend** [2/4]:
   - frontend-maven-plugin installiert Node 18.19.1 + npm 10.2.3
   - `npm install` (943 packages in ~4s)
   - `ng build --configuration production` (~9s)
   - Output: dist/ahp-frontend/* (603 KB initial)
   - maven-resources-plugin kopiert → ahp-backend/src/main/resources/static/

2. **ahp-core** [3/4]:
   - Maven compile & package (~5s)
   - JAR: ahp-core-2.0.0-SNAPSHOT.jar

3. **ahp-backend** [4/4]:
   - Maven compile & package (~19s)
   - Jib dockerBuild:
     - Base Image: eclipse-temurin:21-jre-alpine
     - Container Image: ahp-backend:latest (264 MB)
     - Port: 8080
     - Environment: SPRING_PROFILES_ACTIVE=docker

**Gesamt-Build-Zeit**: ~47-50 Sekunden

### Behobene Probleme (heute)

- ✅ SecurityConfig: AntPathRequestMatcher für statische Ressourcen + H2-Console
- ✅ Frontend-Output-Pfad: dist/ahp-frontend (nicht dist/.../browser)
- ✅ Maven-resources-plugin: Korrekte Copy-Konfiguration
- ✅ Port-Unifikation: Alles auf 8080 (Backend serviert Frontend)
- ✅ Node-Version: 18.19.1 (Angular 18 Kompatibilität)

### Container-Architektur

**docker-compose.dev.yml Services:**
- postgres (PostgreSQL 16-alpine)
- mailhog (MailHog latest)
- ahp-backend (ahp-backend:latest via Jib)

**Netzwerk**: 10_masterthesisahp_ahp-network-dev

## 📁 Wichtige Dateien

### Geändert (heute):
- `ahp-backend/src/main/java/de/x132/ahp/config/SecurityConfig.java`
  - AntPathRequestMatcher für statische Ressourcen
  - H2-Console Support
- `ahp-frontend/pom.xml`
  - Korrekter Output-Pfad: dist/ahp-frontend
- `pom.xml` (Root)
  - Node Version: v18.19.1, npm: 10.2.3

### Key Files:
- `ahp-backend/pom.xml` - Jib Maven Plugin Config
- `ahp-frontend/pom.xml` - frontend-maven-plugin (eirslett)
- `docker-compose.dev.yml` - 3 Services (postgres, mailhog, ahp-backend)
- `BUILD.md` - Build-Dokumentation
- `QUICKSTART.md` - Quick Start Guide

## 📊 Performance

- Backend-Start: ~8 Sekunden (im Container)
- Frontend (Production): 603 KB initial, 127 KB gzipped
- Build-Zeit: ~47 Sekunden (vollständig)
- Container-Image: 264 MB

## 🔗 Dokumentation

- [QUICKSTART.md](QUICKSTART.md) - Quick Start Guide
- [BUILD.md](BUILD.md) - Build & Deploy Guide  
- [DEV_START.md](DEV_START.md) - Development ohne Container
- [SECURITY.md](SECURITY.md) - Security Configuration
- [README.md](README.md) - Projekt-Übersicht
