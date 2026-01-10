# Security Configuration

## Environment Variables

This project uses environment variables for sensitive configuration. **Never commit secrets to version control!**

### Setup

1. Copy the template file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your actual values:
   ```bash
   nano .env
   ```

3. Set secure passwords:
   - `DB_PASSWORD`: Strong password for PostgreSQL (min. 16 characters)
   - `JWT_SECRET`: At least 256-bit secret key for JWT tokens
   - Generate secure secrets: `openssl rand -base64 32`

### Required Variables

#### Production (`docker-compose.yml`)
```bash
# Database
DB_USER=ahp_user
DB_PASSWORD=<your-secure-password>
DB_NAME=ahp_db

# Security
JWT_SECRET=<your-jwt-secret-256-bits-minimum>
```

#### Development
For local development, `docker-compose.dev.yml` uses hardcoded values (ahp_user/ahp_password). This is **only acceptable for local development** - never use these in production!

### Docker Compose Usage

```bash
# Production (requires .env file)
docker compose up -d

# Development (uses hardcoded dev values)
docker compose -f docker-compose.dev.yml up -d
```

### Security Best Practices

- ✅ Use `.env` file for secrets (already in `.gitignore`)
- ✅ Generate strong passwords: `openssl rand -base64 32`
- ✅ Rotate secrets regularly
- ✅ Use different credentials for dev/staging/production
- ❌ Never commit `.env` file
- ❌ Never use default/example passwords in production
- ❌ Never share secrets via email/chat
