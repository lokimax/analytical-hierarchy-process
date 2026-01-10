# 🚀 Development Start

**HINWEIS**: Der Maven-Build dauert nur noch ~45 Sekunden dank Jib! Trotzdem ist lokale Entwicklung für HMR praktischer:

## 1. Nur Dependencies mit Docker starten

```bash
# PostgreSQL und MailHog starten (schon gelaufen)
# Check ob Container bereits laufen
podman ps

# Falls nicht, starten:
# podman run -d --name ahp-postgres-dev -e POSTGRES_DB=ahp_db -e POSTGRES_USER=ahp_user -e POSTGRES_PASSWORD=ahp_password -p 5432:5432 postgres:16-alpine
# podman run -d --name ahp-mailhog-dev -p 1025:1025 -p 8025:8025 mailhog/mailhog:latest
```

## 2. Backend OHNE Docker starten

```bash
cd ahp-backend
mvn spring-boot:run
```

Das startet das Backend auf **Port 9000** mit dem **default** Profil.
Die application.yml verwendet automatisch:
- **PostgreSQL** auf localhost:5432
- **MailHog SMTP** auf localhost:1025

✅ Backend ist bereit wenn du siehst: "Started AhpApplication in X seconds"

## 3. Frontend OHNE Docker starten

```bash
cd ahp-frontend
npm start
```

Das Frontend läuft dann auf **Port 4200** im Dev-Modus mit:
✅ **Hot Module Replacement (HMR)** - Änderungen werden live angezeigt
✅ **Source Maps** - Einfaches Debugging
✅ **Öffnet automatisch Browser** mit --open Flag

## Zugriffspunkte

- **Frontend**: http://localhost:4200 (mit HMR - Hot Module Replacement)
- **Backend API**: http://localhost:9000/api
- **Backend Health**: http://localhost:9000/actuator/health
- **MailHog (Email Testing)**: http://localhost:8025
- **PostgreSQL**: localhost:5432 (DB: ahp_db, User: ahp_user, PW: ahp_password)

## Schnelle Checks

```bash
# Backend Health
curl http://localhost:9000/actuator/health

# Läuft Frontend?
curl http://localhost:4200

# MailHog Web UI
xdg-open http://localhost:8025
```

## Backend API URL im Frontend

Das Frontend ist so konfiguriert, dass es auf `http://localhost:9000/api` zugreift.

## Email-Aktivierung testen

1. **Registrieren**: http://localhost:4200/register
2. **Email checken**: http://localhost:8025 (MailHog)
3. **Aktivierungs-Link klicken** in der Email (Format: http://localhost:8080/#/activate?token=...)
4. **Login**: http://localhost:4200/login

Hinweise:
- Passwort muss mindestens 8 Zeichen haben (Server-Validierung). Vermeide z.B. „default“ (7 Zeichen) – das führt zu 400 Bad Request.
- „No token found in localStorage“ im Browser-Log ist bei Registrierung/Login normal (öffentliche Endpunkte). Der Token existiert erst nach erfolgreichem Login.
- Direkte Aktivierungs-Links nutzen Hash-Routing (`#/activate?token=...`), damit der SPA-Router greift, wenn Frontend über das Backend (Port 8080) ausgeliefert wird.

## Vorteile dieser Methode

✅ **Schneller Start** - Keine Docker Builds nötig
✅ **Hot-Reload** - Änderungen werden sofort sichtbar
✅ **Schnelleres Debugging** - Direkter Zugriff auf Prozesse
✅ **Weniger Ressourcen** - Nur Dependencies in Docker

## Container stoppen

```bash
# Alle stoppen
podman stop ahp-postgres-dev ahp-mailhog-dev

# Alle löschen
podman rm ahp-postgres-dev ahp-mailhog-dev
```

## Zurück zu Docker

Wenn du später alles in Docker haben willst (dauert beim ersten Mal 5-10 Min):

```bash
# Lokale Container stoppen
podman stop ahp-postgres-dev ahp-mailhog-dev

# Mit docker-compose alles starten (dauert beim ersten Mal lange!)
./docker-manage.sh smooth --build
```
