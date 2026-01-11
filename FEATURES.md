# AHP Application - Feature Roadmap

**Priority Scale:** ⭐⭐⭐⭐⭐ = MOST CRITICAL | ⭐⭐⭐⭐ = High | ⭐⭐⭐ = Medium | ⭐⭐ = Low | ⭐ = Nice-to-Have

---

## 1. Core Analysis Features

### Sensitivity Analysis
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (PR #3 merged 2026-01-10)
**Description:** Analyze how changes in pairwise comparisons affect the final rankings. Show sensitivity graphs and identify critical comparison values.
**Implementation:**
- ✅ Backend: SensitivityAnalysisService with weight variation (0-100%)
- ✅ Frontend: Interactive Chart.js visualization
- ✅ Critical point detection (ranking changes)
- ✅ Stability metrics (score, risk level, tolerance range)
- ✅ REST Endpoint: GET /api/projects/{name}/analyses/{id}/sensitivity/{criterionId}
- ✅ Route: /projects/:projectName/analyses/:analysisId/sensitivity/:criterionId
- ✅ 17 tests passing (9 backend + 8 frontend)

**Enhancement Opportunities:**
- 📌 Export sensitivity analysis to PDF/Excel
- 📌 Compare multiple criteria side-by-side
- 📌 Advanced chart annotations and tooltips
- 📌 Save/load sensitivity analysis results
- 📌 Sensitivity matrix for all criteria pairs

### What-If Analysis
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Scenario planning tool to test different comparison matrices and see impact on final results.

### Consistency Ratio Calculation
**Priority:** ⭐⭐⭐⭐
**Status:** ✅ Completed (Session: improving_tests)
**Description:** Calculate and display consistency ratios for each level of the hierarchy. Implemented as ConsistencyRatioService.

---

## 1.1. Advanced Solving Methods

### TOPSIS (Technique for Order Preference by Similarity)
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Find alternatives closest to ideal solution and farthest from worst. No pairwise comparisons needed - direct rating approach. Simple, intuitive, fast computation. Perfect complement to AHP for different decision-making styles.

### BWM (Best-Worst Method)
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Modern alternative to AHP requiring only 2n-3 comparisons instead of n(n-1)/2. Better consistency than AHP. Compare only with best and worst criteria. Reduces cognitive load significantly.

### Analytic Network Process (ANP)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started (Enum exists)
**Description:** Generalization of AHP allowing feedback loops and interdependencies between criteria. Uses supermatrix calculation for complex networks. Essential for real-world problems with dependencies.

### PROMETHEE (Preference Ranking Organization Method)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Flexible pairwise comparisons with 6 preference function types (linear, Gaussian, U-shape, etc.). Handles both qualitative and quantitative criteria. PROMETHEE I for partial ranking, II for complete ranking.

### ELECTRE (Elimination and Choice Expressing Reality)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Outranking method using concordance and discordance indices. Good for complex problems with many criteria. Multiple variants (I, II, III, IV, TRI) for selection, ranking, and sorting.

### Fuzzy AHP
**Priority:** ⭐⭐⭐
**Status:** Not Started (Enum exists)
**Description:** Handle uncertainty in pairwise comparisons using fuzzy numbers (triangular/trapezoidal). Better for vague or imprecise judgments where exact ratios are difficult to determine.

### Weighted Sum Method (WSM) / SAW
**Priority:** ⭐⭐⭐
**Status:** Not Started (Enum exists)
**Description:** Simple weighted sum approach for multi-criteria decision making. Alternative to classic AHP eigenvector method. Faster computation for simple hierarchies. Transparent and easy to understand.

### VIKOR (Compromise Ranking)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Finds compromise solution balancing "majority rule" (group utility) and "minimal regret" (individual satisfaction). Similar to TOPSIS but focuses on finding acceptable compromises in conflicting criteria scenarios.

### DEMATEL (Decision Making Trial and Evaluation)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Analyze cause-effect relationships between criteria. Shows interdependencies and influence diagrams. Excellent for problem structuring and understanding criterion relationships before applying other MCDM methods.

### SMART (Simple Multi-Attribute Rating)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Very simple direct rating + weighting approach. No pairwise comparisons, no consistency checks. Fast decisions for simple problems. Good for quick preliminary analysis or training purposes.

### MACBETH (Measuring Attractiveness by Categories)
**Priority:** ⭐⭐
**Status:** Not Started
**Description:** Uses semantic categories (weak, moderate, strong) instead of numeric ratios. Easier than numeric comparisons for some users. Includes M-MACBETH software tool integration possibility.

### Goal Programming
**Priority:** ⭐⭐
**Status:** Not Started
**Description:** Optimize multiple goals with priorities and constraints. Lexicographic, weighted, and Chebyshev variants. More suited for Operations Research problems with optimization focus rather than pure decision support.

### DEA (Data Envelopment Analysis)
**Priority:** ⭐⭐
**Status:** Not Started
**Description:** Efficiency frontier analysis for benchmarking. Compares decision-making units (DMUs) and outputs efficiency scores (0-100%). Best for performance measurement of similar entities (hospitals, universities, etc.).

### Grey Relational Analysis (GRA)
**Priority:** ⭐
**Status:** Not Started
**Description:** Decision making under uncertainty with incomplete data. Good for small datasets and fuzzy environments. Niche application for manufacturing and quality engineering scenarios.

---

## 2. Data Visualization

### Interactive Charts & Graphs
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Visualize final rankings with bar charts, pie charts, and hierarchical tree views.

### Comparison Matrix Visualization
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Visual representation of pairwise comparison matrices (heatmaps, tables).

### Hierarchy Diagram
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Display the AHP hierarchy structure as an interactive diagram.

---

## 3. Project Management

### Create Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed
**Description:** Create new AHP projects with custom hierarchies.

### Edit Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (Session: feature/edit-delete-project)
**Description:** Modify project names and descriptions. Frontend UI with edit button, dual-mode form (create/update).

### Delete Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (Session: feature/edit-delete-project)
**Description:** Remove projects with confirmation dialog to prevent accidental deletion.

### Project Sharing
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Share projects with other users for collaborative analysis.

---

## 4. User Experience

### Mobile Responsive Design
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Ensure full functionality on tablets and mobile devices.

### Dark Mode
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Theme toggle for dark/light mode.

### Undo/Redo Functionality
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Navigate through action history within a project.

---

## 5. Export & Reporting

### PDF Export
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Export analysis results to PDF with charts and tables.

### Excel Export
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Export comparison matrices and results to Excel format.

### Custom Reports
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Generate customizable analysis reports with user-selected sections.

---

## 6. API & Integration

### API Documentation (Swagger)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Complete OpenAPI/Swagger documentation for REST API endpoints.

### GraphQL API
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Alternative GraphQL endpoint for flexible data querying.

### Third-party Integration
**Priority:** ⭐⭐
**Status:** Not Started
**Description:** Integration with external tools (Jira, Trello, etc.).

---

## 7. Advanced Analytics

### Multi-Criteria Sorting
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Sort and filter alternatives by multiple criteria.

### Benchmark Comparison
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Compare current analysis against historical benchmarks or industry standards.

### Monte Carlo Simulation
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Run probabilistic simulations on pairwise comparisons for uncertainty analysis.

---

## 8. Administration & Security

### User Role Management
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Admin, Manager, Analyst, Viewer roles with permission control.

### Audit Logging
**Priority:** ⭐⭐⭐⭐
**Status:** ✅ Completed (PR #5 in review 2026-01-11)
**Description:** Track all user actions and changes for compliance and debugging.
**Implementation:**
- ✅ Hibernate Envers integration with custom revision tracking
- ✅ 7 audited entities: Project, Node, Connection, Prioritisation, Comparison, Analysis, Client
- ✅ AuditRevision entity with user and timestamp tracking
- ✅ AuditService with getEntityRevisions, getEntityHistory, findEntityAtRevision endpoints
- ✅ 15+ REST endpoints for audit history queries
- ✅ Entity ownership validation and security controls
- ✅ Role-based access control (USER/ADMIN)
- ✅ Comprehensive test coverage (38 tests total)

### Backup & Recovery
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Automated backup and disaster recovery mechanisms.

---

## 9. Authentication & User Management

### Password Reset
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Forgot password functionality. Send reset link via email. Secure token-based password change without login.

### Password Confirmation on Registration
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Require users to confirm password twice during registration. Real-time validation and visual feedback for matching/mismatching passwords.

### Show/Hide Password Toggle
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** Eye icon toggle to reveal/hide password during login and registration. Improves usability while maintaining security.

### OAuth 2.0 Social Login
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Description:** Authenticate with external providers: Google, Facebook, Twitter/X. Automatic user profile creation. Streamlined login experience without password management.

### Two-Factor Authentication (2FA)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Description:** TOTP-based (Google Authenticator, Authy) or SMS-based 2FA for enhanced security. Optional enforcement per user role.

### Account Lockout Protection
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Description:** Automatic temporary lockout after failed login attempts. Progressive delays to prevent brute force attacks.

---

## Top 5 MOST CRITICAL (Next Focus)

1. ✅ **Edit Project** - Complete (2026-01-10)
2. ✅ **Delete Project** - Complete (2026-01-10)
3. ✅ **Sensitivity Analysis** - Complete (PR #3 merged 2026-01-10)
4. ✅ **Show/Hide Password Toggle** - Complete (PR #4 merged 2026-01-10)
5. ✅ **Audit Logging** - Complete (PR #5 in review 2026-01-11)

---

## Authentication & Security Features (Priority Order)

**⭐⭐⭐⭐⭐ MOST CRITICAL:**
- ✅ **Show/Hide Password Toggle** - Completed (PR #4 merged 2026-01-10)
- OAuth 2.0 Social Login (Google, Facebook, Twitter)
- Password Reset via Email
- Password Confirmation on Registration

**⭐⭐⭐⭐ HIGH:**
- Two-Factor Authentication (2FA)
- Account Lockout Protection
- Session Management & Timeout

**⭐⭐⭐ MEDIUM:**
- Password Strength Requirements
- Security Questions
- Biometric Login (Fingerprint/Face)

---

## Recently Completed Improvements (Session: improving_tests) ✅

### Docker & Containerization
- ✅ Docker Compose setup with Jib
- ✅ PostgreSQL, nginx, backend containers
- ✅ docker-manage.sh script for build/run/clean

### Testing Infrastructure
- ✅ Frontend Tests: 150 tests passing (Jasmine/Karma)
  - Component tests (53 tests)
  - AuthGuard tests (5 tests)
  - Service tests
  - Integration tests
- ✅ Backend Tests: 63 tests passing (JUnit5)
  - Controller tests
  - Service tests
  - Integration tests
  - E2E tests
- ✅ Total: 213 tests passing

### Code Quality
- ✅ MapStruct for DTO mapping
- ✅ Spotless Maven plugin for code formatting
- ✅ Consistent code style across modules

### AHP Core Features
- ✅ ConsistencyRatio Service implementation
- ✅ AHP validation logic
- ✅ Error handling improvements
- ✅ Loading states in frontend

---

## Completed Foundation Features ✅
- ✅ Project CRUD (Create, Read, Update, Delete)
- ✅ User registration and activation flow
- ✅ Authentication and authorization (JWT)
- ✅ AHP calculation engine (consistency ratio, final rankings)
- ✅ Comparison matrix input UI
- ✅ Basic result display
- ✅ Error handling and loading states
- ✅ PostgreSQL database persistence
- ✅ RESTful API with Spring Boot
- ✅ Angular 18 frontend with Signals

---

**Last Updated:** 11. Januar 2026 10:45
**Latest Session:** PR #5 in review - Comprehensive Audit Logging with Hibernate Envers + Security Controls
