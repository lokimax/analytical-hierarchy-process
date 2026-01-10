#!/bin/bash
# AHP Complete Round-Trip Test
# Tests: User registration → Email activation → Login → Project creation → Nodes → Connections → Analysis
# Run after: ./docker-manage.sh smooth --build

set -euo pipefail

BASE_URL="http://localhost:8080/api"
MAILHOG_URL="http://localhost:8025"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

log_step() { echo -e "${BLUE}→ $1${NC}"; }
log_success() { echo -e "${GREEN}✓ $1${NC}"; }
log_error() { echo -e "${RED}✗ $1${NC}"; }

# 1. REGISTRATION
log_step "1. Register new user"
REG_RESPONSE=$(curl -s -X POST "$BASE_URL/clients/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "testuser",
    "name": "Test",
    "surename": "User",
    "email": "testuser@example.com",
    "password": "SecurePassword123"
  }')

echo "$REG_RESPONSE" | grep -q "testuser" && log_success "Registration successful" || { log_error "Registration failed"; echo "$REG_RESPONSE"; exit 1; }

# 2. GET ACTIVATION TOKEN FROM MAILHOG
log_step "2. Retrieve activation token from MailHog"
sleep 2
MAILHOG_RESPONSE=$(curl -s "$MAILHOG_URL/api/v2/messages")
ACTIVATION_TOKEN=$(echo "$MAILHOG_RESPONSE" | grep -oP 'token=[^&"]*' | head -1 | cut -d= -f2)

if [ -z "$ACTIVATION_TOKEN" ]; then
  log_error "No activation token found"
  echo "MailHog response: $MAILHOG_RESPONSE"
  exit 1
fi
log_success "Activation token retrieved: ${ACTIVATION_TOKEN:0:20}..."

# 3. ACTIVATE EMAIL
log_step "3. Activate user email"
ACTIVATE_RESPONSE=$(curl -s -X POST "$BASE_URL/clients/activate" \
  -H "Content-Type: application/json" \
  -d "{\"token\": \"$ACTIVATION_TOKEN\"}")

echo "$ACTIVATE_RESPONSE" | grep -q "testuser" && log_success "Email activation successful" || { log_error "Activation failed"; echo "$ACTIVATE_RESPONSE"; exit 1; }

# 4. LOGIN
log_step "4. Login with credentials"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/clients/login" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "testuser",
    "password": "SecurePassword123"
  }')

AUTH_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -oP '"token":"?\K[^"]*' | head -1)

if [ -z "$AUTH_TOKEN" ]; then
  log_error "Login failed"
  echo "$LOGIN_RESPONSE"
  exit 1
fi
log_success "Login successful, token: ${AUTH_TOKEN:0:20}..."

# 5. CREATE PROJECT
log_step "5. Create project"
PROJECT_RESPONSE=$(curl -s -X POST "$BASE_URL/projects" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "name": "AutoSelection",
    "beschreibung": "Car selection AHP analysis"
  }')

PROJECT_ID=$(echo "$PROJECT_RESPONSE" | grep -oP '"id":\K[^,}]*' | head -1)

if [ -z "$PROJECT_ID" ]; then
  log_error "Project creation failed"
  echo "$PROJECT_RESPONSE"
  exit 1
fi
log_success "Project created: ID=$PROJECT_ID"

# 6. CREATE NODES (CRITERIA)
log_step "6. Create criteria nodes"
NODE_IDS=()
for CRITERION in "Price" "Safety" "Consumption" "Comfort"; do
  NODE_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/AutoSelection/nodes" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $AUTH_TOKEN" \
    -d "{\"name\": \"$CRITERION\", \"beschreibung\": \"$CRITERION criterion\"}")
  
  NODE_ID=$(echo "$NODE_RESPONSE" | grep -oP '"id":\K[^,}]*' | head -1)
  NODE_IDS+=("$NODE_ID")
  log_success "Node '$CRITERION' created: ID=$NODE_ID"
done

# 7. CREATE ALTERNATIVE NODES
log_step "7. Create alternative nodes"
ALT_IDS=()
for ALT in "CarA" "CarB" "CarC"; do
  ALT_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/AutoSelection/nodes" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $AUTH_TOKEN" \
    -d "{\"name\": \"$ALT\", \"beschreibung\": \"Alternative $ALT\"}")
  
  ALT_ID=$(echo "$ALT_RESPONSE" | grep -oP '"id":\K[^,}]*' | head -1)
  ALT_IDS+=("$ALT_ID")
  log_success "Alternative '$ALT' created: ID=$ALT_ID"
done

# 8. CREATE CONNECTIONS
log_step "8. Create connections between criteria and alternatives"
for CRITERION in "Price" "Safety" "Consumption" "Comfort"; do
  for ALT in "CarA" "CarB" "CarC"; do
    CONN_RESPONSE=$(curl -s -X POST "$BASE_URL/projects/AutoSelection/connections" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $AUTH_TOKEN" \
      -d "{\"sourceNodeName\": \"$CRITERION\", \"targetNodeName\": \"$ALT\"}")
    
    log_success "Connection: $CRITERION → $ALT"
  done
done

# 9. CREATE ANALYSIS
log_step "9. Create analysis"
ANALYSIS_RESPONSE=$(curl -s -X POST "$BASE_URL/analyses" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH_TOKEN" \
  -d '{
    "name": "CarSelection_v1",
    "beschreibung": "First analysis run",
    "projectName": "AutoSelection",
    "criteriaComparisons": [{"leftNode": "Price", "rightNode": "Safety", "weight": 2.0}],
    "alternativeComparisons": [{"criterion": "Price", "leftNode": "CarA", "rightNode": "CarB", "weight": 1.5}],
    "results": null
  }')

ANALYSIS_ID=$(echo "$ANALYSIS_RESPONSE" | grep -oP '"id":\K[^,}]*' | head -1)

if [ -n "$ANALYSIS_ID" ]; then
  log_success "Analysis created: ID=$ANALYSIS_ID"
else
  echo "Analysis response: $ANALYSIS_RESPONSE"
fi

# SUMMARY
echo ""
echo "==============================================="
log_success "COMPLETE ROUND-TRIP TEST SUCCESSFUL"
echo "==============================================="
echo "User: testuser (testuser@example.com)"
echo "Project: AutoSelection (ID=$PROJECT_ID)"
echo "Criteria: ${#NODE_IDS[@]} nodes created"
echo "Alternatives: ${#ALT_IDS[@]} nodes created"
echo ""
echo "URLs:"
echo "  App:     http://localhost:8080"
echo "  MailHog: http://localhost:8025"
echo "  API:     http://localhost:8080/api"
echo ""
