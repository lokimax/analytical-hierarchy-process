# Docker Installation Guide für Linux (Ubuntu/Debian)

Dieses Projekt erfordert Docker und Docker Compose für die Containerisierung.

## Installation

### Option 1: Docker Desktop (Empfohlen für Anfänger)

1. **Download**: https://www.docker.com/products/docker-desktop/
2. **Installation**: Folge der Installer-Schritte
3. **Starten**: Starte die Docker Desktop App
4. **Prüfen**:
   ```bash
   docker --version
   docker compose version
   ```

### Option 2: Docker via APT (Ubuntu/Debian)

Entferne zuerst alte Versionen:
```bash
sudo apt-get remove docker docker-engine docker.io containerd runc
```

Installiere Docker Engine:
```bash
# Update package manager
sudo apt-get update

# Install dependencies
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# Add Docker's GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
```

Prüfe die Installation:
```bash
docker --version
docker compose version
```

### Option 3: Docker via Snap (Schnellste Installation)

```bash
sudo snap install docker
docker --version
```

**Hinweis**: Snap Docker ist manchmal langsamer. Verwende Option 2 für bessere Performance.

## Post-Installation Setup

### 1. Docker Daemon starten

```bash
# Start Docker service
sudo systemctl start docker

# Start on boot
sudo systemctl enable docker

# Verify running
sudo systemctl status docker
```

### 2. Docker ohne sudo verwenden

```bash
# Add your user to docker group
sudo usermod -aG docker $USER

# Apply new group
newgrp docker

# Verify (keine sudo nötig)
docker ps
```

### 3. Docker Compose separates Installation (falls nötig)

Falls nur Docker installiert ist, aber nicht Docker Compose:

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version
```

Oder mit Pip:
```bash
pip install docker-compose
```

## Verifikation

Prüfe, dass alles funktioniert:

```bash
# Docker Status
docker ps

# Docker Version
docker --version

# Docker Compose Version (eine dieser Befehle sollte funktionieren)
docker compose version    # Docker Compose v2
docker-compose version    # Docker Compose v1
```

## Troubleshooting

### "Cannot connect to Docker daemon"

```bash
# Docker-Daemon ist nicht gestartet
sudo systemctl start docker

# Oder mit Docker Desktop: Starte die App
```

### "Permission denied while trying to connect to Docker daemon"

```bash
# User ist nicht in docker group
sudo usermod -aG docker $USER
newgrp docker

# Dann neuen Shell-Tab öffnen oder neu anmelden
```

### "docker-compose: command not found"

```bash
# Docker Compose ist nicht installiert
# Versuche mit 'docker compose' (neue Version):
docker compose version

# Oder installiere docker-compose-plugin:
sudo apt-get install docker-compose-plugin
```

### Alte docker-compose Version

Falls `docker-compose` (v1) installiert ist, upgrade auf Docker Compose v2:

```bash
# Entferne alte Version
sudo apt-get remove docker-compose

# Installiere neue Version
sudo apt-get install docker-compose-plugin

# Verwende neue Syntax
docker compose up -d   # statt docker-compose up -d
```

## Ressourcen

- Docker Docs: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/
- Installation Guide: https://docs.docker.com/engine/install/

---

Nach Installation, kannst du direkt starten:

```bash
cd /path/to/10_MasterThesisAHP
docker-compose up -d
```

Oder mit Management-Script:
```bash
./docker-manage.sh start
./docker-manage.sh logs
```
