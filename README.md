# 📊 Analytical Hierarchy Process (AHP)

[![Deploy to Fly (dev)](https://github.com/lokimax/analytical-hierarchy-process/actions/workflows/deploy-fly-dev.yml/badge.svg?branch=develop)](https://github.com/lokimax/analytical-hierarchy-process/actions/workflows/deploy-fly-dev.yml)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18-DD0031?logo=angular&logoColor=white)](https://angular.io/)

Eine Webanwendung zur Entscheidungsfindung mit dem Analytical Hierarchy Process nach Thomas L. Saaty.

## 🧭 Einführung in AHP

Der Analytical Hierarchy Process (AHP) ist eine strukturierte Technik zur Analyse und Organisation komplexer Entscheidungsprobleme. Die Methode wurde von Thomas L. Saaty in den 1970er Jahren entwickelt und basiert auf mathematischen und psychologischen Prinzipien.

### ⚙️ Funktionsweise

1. **Hierarchische Strukturierung**: Das Entscheidungsproblem wird in eine Hierarchie aus Ziel, Kriterien und Alternativen zerlegt
2. **Paarweiser Vergleich**: Kriterien und Alternativen werden paarweise miteinander verglichen
3. **Gewichtung**: Durch die Vergleiche werden relative Gewichte für jedes Element berechnet
4. **Priorisierung**: Die Methode ermittelt eine Rangfolge der Alternativen basierend auf den gewichteten Kriterien

### 📏 Vergleichsskala

Bei paarweisen Vergleichen wird die Saaty-Skala verwendet:
- **1** - Gleiche Bedeutung
- **3** - Mäßige Bedeutung
- **5** - Starke Bedeutung
- **7** - Sehr starke Bedeutung
- **9** - Extreme Bedeutung
- **2, 4, 6, 8** - Zwischenwerte

## 🧰 Technologie-Stack

- **Backend**: Spring Boot 3.1.5, Java 21, Maven 3.9+
- **Frontend**: Angular 18 (Standalone Components)
- **Datenbank**: PostgreSQL 16
- **Container**: Jib Maven Plugin (Docker/Podman)
- **Email**: MailHog (Development)
- **Sicherheit**: JWT-basierte Authentifizierung, Email-Aktivierung

## 🚀 Schnellstart

### ✅ Voraussetzungen

- **Maven 3.9+**
- **Podman** oder **Docker**
- **Git**

### 🏗️ Build & Start

```bash
# 1. Repository klonen
git clone https://github.com/lokimax/analytical-hierarchy-process.git
cd analytical-hierarchy-process

# 2. Alles bauen (Frontend + Backend + Container-Image)
mvn clean package -DskipTests

# 3. Services starten
podman-compose -f docker-compose.dev.yml up -d

# 4. Browser öffnen
xdg-open http://localhost:8080
```

Das wars! Die Anwendung läuft auf **http://localhost:8080**

## 🧪 Durchführung eines Vergleichs

### 1. 🧑‍💻 Registrierung und Anmeldung

- Öffnen Sie **http://localhost:4200** im Browser
- Klicken Sie auf **"Register"**
- Erstellen Sie einen Account mit Benutzername und Passwort
- Melden Sie sich mit Ihren Zugangsdaten an

### 2. 🗂️ Projekt erstellen

- Klicken Sie auf **"New Project"**
- Geben Sie einen Projektnamen ein (z.B. "Auto-Auswahl")
- Fügen Sie eine Beschreibung hinzu
- Speichern Sie das Projekt

### 3. 🧱 Hierarchie aufbauen

#### 🔹 Kriterien definieren
- Wechseln Sie zur **"Nodes"**-Ansicht
- Erstellen Sie Kriterien als Knoten (z.B. "Preis", "Sicherheit", "Verbrauch", "Komfort")
- Definieren Sie Unterkriterien falls nötig (hierarchische Struktur möglich)

#### 🔸 Alternativen definieren
- Erstellen Sie Blattknoten für die zu bewertenden Alternativen (z.B. "Auto A", "Auto B", "Auto C")

#### 🔗 Verbindungen erstellen
- Wechseln Sie zur **"Connections"**-Ansicht
- Verbinden Sie Kriterien mit den Alternativen
- Die Verbindungen definieren, welche Alternativen nach welchen Kriterien bewertet werden

### 4. 🧮 Paarweise Vergleiche durchführen

- Wechseln Sie zur **"Analysis"**-Ansicht
- Erstellen Sie eine neue Analyse
- Das System führt Sie durch die paarweisen Vergleiche:

#### 📊 Kriterienvergleich
- Vergleichen Sie jeweils zwei Kriterien miteinander
- Frage: "Wie wichtig ist Kriterium A im Vergleich zu Kriterium B?"
- Nutzen Sie den Schieberegler (1-9 Skala)

#### 🏁 Alternativenvergleich
- Für jedes Kriterium: Vergleichen Sie die Alternativen paarweise
- Frage: "Wie gut schneidet Alternative A bei Kriterium X im Vergleich zu Alternative B ab?"

### 5. 📈 Ergebnisse anzeigen

- Nach Abschluss aller Vergleiche werden die Ergebnisse automatisch berechnet
- Die **"Results"**-Ansicht zeigt:
  - **Gewichtungen der Kriterien** (in Prozent)
  - **Bewertungen der Alternativen** für jedes Kriterium
  - **Gesamtranking** der Alternativen
  - **Spider-Chart** zur visuellen Darstellung der Alternativenbewertung
  - **Konsistenzindex (CI)** zur Überprüfung der Vergleichsqualität

### 6. ✅ Konsistenz prüfen

Ein wichtiger Aspekt der AHP-Methode ist die Konsistenzprüfung:
- **CI < 0.1**: Vergleiche sind konsistent und akzeptabel
- **CI > 0.1**: Vergleiche sollten überprüft und ggf. angepasst werden

Bei inkonsistenten Vergleichen können Sie die Analyse wiederholen und Anpassungen vornehmen.

## 🏛️ Architektur

### 🧩 Backend-Module

- **ahp-core**: Kernalgorithmus für AHP-Berechnungen mit Apache Commons Math
- **ahp-backend**: REST API, Authentifizierung, Datenverwaltung

### 🖼️ Frontend-Struktur

- **Standalone Components**: Moderne Angular 18 Architektur
- **Reactive Forms mit Signals**: Typsichere Formularverarbeitung
- **JWT-Authentifizierung**: Sichere Client-Server-Kommunikation
- **Bootstrap 5 UI**: Responsive Design

## 🧑‍🔧 Entwicklung

### 🧪 Tests ausführen

Backend-Tests:
```bash
cd ahp-backend
mvn test
```

Frontend-Tests:
```bash
cd ahp-frontend
npm test
```

### 🏭 Build für Produktion

Backend:
```bash
cd ahp-backend
mvn clean package
```

Frontend:
```bash
cd ahp-frontend
ng build --configuration production
```

## 📜 Lizenz

Siehe [LICENSE](LICENSE) Datei.

## 👤 Autor

Master Thesis Projekt - Analytical Hierarchy Process Implementation