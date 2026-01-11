# AHP Application - Feature Roadmap

**Priority Scale:** ⭐⭐⭐⭐⭐ = MOST CRITICAL | ⭐⭐⭐⭐ = High | ⭐⭐⭐ = Medium | ⭐⭐ = Low | ⭐ = Nice-to-Have

**Estimation Scale:** 
- 🟢 S (1-3 pts) = Quick win, < 1 day
- 🟡 M (5-8 pts) = Standard feature, 1-3 days
- 🟠 L (13 pts) = Complex feature, 1 week
- 🔴 XL (21+ pts) = Major feature, 2+ weeks

---

## 1. Core Analysis Features

### Sensitivity Analysis
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (PR #3 merged 2026-01-10)
**Estimate:** 🟡 M (5 pts) - Already done
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
**Estimate:** 🟠 L (13 pts) - 1 week
**Description:** Scenario planning tool to test different comparison matrices and see impact on final results.

### Consistency Ratio Calculation
**Priority:** ⭐⭐⭐⭐
**Status:** ✅ Completed (Session: improving_tests)
**Estimate:** 🟡 M (8 pts) - Already done
**Description:** Calculate and display consistency ratios for each level of the hierarchy. Implemented as ConsistencyRatioService.

---

## 1.1. Advanced Solving Methods

### TOPSIS (Technique for Order Preference by Similarity)
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Service + Controller + Tests + UI)
**Description:** Find alternatives closest to ideal solution and farthest from worst. No pairwise comparisons needed - direct rating approach. Simple, intuitive, fast computation. Perfect complement to AHP for different decision-making styles.

### BWM (Best-Worst Method)
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Similar to TOPSIS but simpler Matrix)
**Description:** Modern alternative to AHP requiring only 2n-3 comparisons instead of n(n-1)/2. Better consistency than AHP. Compare only with best and worst criteria. Reduces cognitive load significantly.

### Analytic Network Process (ANP)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started (Enum exists)
**Estimate:** 🔴 XL (21 pts) - 2-3 weeks (Supermatrix calculations complex)
**Description:** Generalization of AHP allowing feedback loops and interdependencies between criteria. Uses supermatrix calculation for complex networks. Essential for real-world problems with dependencies.

### PROMETHEE (Preference Ranking Organization Method)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🔴 XL (21 pts) - 2-3 weeks (6 preference function types)
**Description:** Flexible pairwise comparisons with 6 preference function types (linear, Gaussian, U-shape, etc.). Handles both qualitative and quantitative criteria. PROMETHEE I for partial ranking, II for complete ranking.

### ELECTRE (Elimination and Choice Expressing Reality)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🔴 XL (21 pts) - 2-3 weeks (Concordance/Discordance complex)
**Description:** Outranking method using concordance and discordance indices. Good for complex problems with many criteria. Multiple variants (I, II, III, IV, TRI) for selection, ranking, and sorting.

### Fuzzy AHP
**Priority:** ⭐⭐⭐
**Status:** Not Started (Enum exists)
**Estimate:** 🟠 L (13 pts) - 1 week (Triangular/Trapezoidal numbers)
**Description:** Handle uncertainty in pairwise comparisons using fuzzy numbers (triangular/trapezoidal). Better for vague or imprecise judgments where exact ratios are difficult to determine.

### Weighted Sum Method (WSM) / SAW
**Priority:** ⭐⭐⭐
**Status:** Not Started (Enum exists)
**Estimate:** 🟢 S (3 pts) - Quick win (simplest method)
**Description:** Simple weighted sum approach for multi-criteria decision making. Alternative to classic AHP eigenvector method. Faster computation for simple hierarchies. Transparent and easy to understand.

### VIKOR (Compromise Ranking)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days
**Description:** Finds compromise solution balancing "majority rule" (group utility) and "minimal regret" (individual satisfaction). Similar to TOPSIS but focuses on finding acceptable compromises in conflicting criteria scenarios.

### DEMATEL (Decision Making Trial and Evaluation)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Influence diagrams visualization)
**Description:** Analyze cause-effect relationships between criteria. Shows interdependencies and influence diagrams. Excellent for problem structuring and understanding criterion relationships before applying other MCDM methods.

### SMART (Simple Multi-Attribute Rating)
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟢 S (3 pts) - Quick win (very simple)
**Description:** Very simple direct rating + weighting approach. No pairwise comparisons, no consistency checks. Fast decisions for simple problems. Good for quick preliminary analysis or training purposes.

### MACBETH (Measuring Attractiveness by Categories)
**Priority:** ⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - 1-2 days
**Description:** Uses semantic categories (weak, moderate, strong) instead of numeric ratios. Easier than numeric comparisons for some users. Includes M-MACBETH software tool integration possibility.

### Goal Programming
**Priority:** ⭐⭐
**Status:** Not Started
**Estimate:** 🔴 XL (21 pts) - 2+ weeks (Optimization algorithms)
**Description:** Optimize multiple goals with priorities and constraints. Lexicographic, weighted, and Chebyshev variants. More suited for Operations Research problems with optimization focus rather than pure decision support.

### DEA (Data Envelopment Analysis)
**Priority:** ⭐⭐
**Status:** Not Started
**Estimate:** 🔴 XL (21 pts) - 2+ weeks (Linear programming)
**Description:** Efficiency frontier analysis for benchmarking. Compares decision-making units (DMUs) and outputs efficiency scores (0-100%). Best for performance measurement of similar entities (hospitals, universities, etc.).

### Grey Relational Analysis (GRA)
**Priority:** ⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - Niche application
**Description:** Decision making under uncertainty with incomplete data. Good for small datasets and fuzzy environments. Niche application for manufacturing and quality engineering scenarios.

---

## 2. Data Visualization

### Interactive Charts & Graphs
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (use Chart.js + D3.js)
**Description:** Visualize final rankings with bar charts, pie charts, and hierarchical tree views.

### Comparison Matrix Visualization
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - 1-2 days (Heatmap component)
**Description:** Visual representation of pairwise comparison matrices (heatmaps, tables).

### Hierarchy Diagram
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Dagrex or similar)
**Description:** Display the AHP hierarchy structure as an interactive diagram.

---

## 3. Project Management

### Create Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed
**Estimate:** 🟢 S (3 pts) - Already done
**Description:** Create new AHP projects with custom hierarchies.

### Edit Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (Session: feature/edit-delete-project)
**Estimate:** 🟡 M (5 pts) - Already done
**Description:** Modify project names and descriptions. Frontend UI with edit button, dual-mode form (create/update).

### Delete Project
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** ✅ Completed (Session: feature/edit-delete-project)
**Estimate:** 🟢 S (2 pts) - Already done
**Description:** Remove projects with confirmation dialog to prevent accidental deletion.

### Project Sharing
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Share logic + Permissions)
**Description:** Share projects with other users for collaborative analysis.

---

## 4. User Experience

### Mobile Responsive Design
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Testing on devices)
**Description:** Ensure full functionality on tablets and mobile devices.

### Dark Mode
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟢 S (3 pts) - 1 day (CSS theming)
**Description:** Theme toggle for dark/light mode.

### Undo/Redo Functionality
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Command pattern)
**Description:** Navigate through action history within a project.

---

## 5. Export & Reporting

### PDF Export
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (PDFKit or similar)
**Description:** Export analysis results to PDF with charts and tables.

### Excel Export
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - 1-2 days (Apache POI or SheetJS)
**Description:** Export comparison matrices and results to Excel format.

### Custom Reports
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Report builder UI + Templates)
**Description:** Generate customizable analysis reports with user-selected sections.

---

## 6. API & Integration

### API Documentation (Swagger)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟢 S (3 pts) - 1 day (SpringDoc OpenAPI)
**Description:** Complete OpenAPI/Swagger documentation for REST API endpoints.

### GraphQL API
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🔴 XL (21 pts) - 2+ weeks (GraphQL schema + resolvers)
**Description:** Alternative GraphQL endpoint for flexible data querying.

### Third-party Integration
**Priority:** ⭐⭐
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week per integration (Jira/Trello API)
**Description:** Integration with external tools (Jira, Trello, etc.).

---

## 7. Advanced Analytics

### Multi-Criteria Sorting
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - 1-2 days (Sorting logic + UI)
**Description:** Sort and filter alternatives by multiple criteria.

### Benchmark Comparison
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Historical data storage)
**Description:** Compare current analysis against historical benchmarks or industry standards.

### Monte Carlo Simulation
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Statistics + Visualization)
**Description:** Run probabilistic simulations on pairwise comparisons for uncertainty analysis.

---

## 8. Administration & Security

### User Role Management
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Roles + Permissions framework)
**Description:** Admin, Manager, Analyst, Viewer roles with permission control.

### Audit Logging
**Priority:** ⭐⭐⭐⭐
**Status:** ✅ Completed (PR #5 in review 2026-01-11)
**Estimate:** 🟡 M (8 pts) - Already done
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
**Estimate:** 🟠 L (13 pts) - 1 week (Backup strategy + Testing)
**Description:** Automated backup and disaster recovery mechanisms.

---

## 9. Authentication & User Management

### Password Reset
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (Email + Token logic)
**Description:** Forgot password functionality. Send reset link via email. Secure token-based password change without login.

### Password Confirmation on Registration
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟢 S (3 pts) - 1 day (Validation only)
**Description:** Require users to confirm password twice during registration. Real-time validation and visual feedback for matching/mismatching passwords.

### Show/Hide Password Toggle
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟢 S (2 pts) - Already done (PR #4)
**Description:** Eye icon toggle to reveal/hide password during login and registration. Improves usability while maintaining security.

### OAuth 2.0 Social Login
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started
**Estimate:** 🟠 L (13 pts) - 1 week (Per provider: Google, Facebook, Twitter)
**Description:** Authenticate with external providers: Google, Facebook, Twitter/X. Automatic user profile creation. Streamlined login experience without password management.

### Two-Factor Authentication (2FA)
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (8 pts) - 2-3 days (TOTP implementation)
**Description:** TOTP-based (Google Authenticator, Authy) or SMS-based 2FA for enhanced security. Optional enforcement per user role.

### Account Lockout Protection
**Priority:** ⭐⭐⭐
**Status:** Not Started
**Estimate:** 🟡 M (5 pts) - 1-2 days (Counter + Delay logic)
**Description:** Automatic temporary lockout after failed login attempts. Progressive delays to prevent brute force attacks.

---

## 10. Architectural Decisions & Migrations

### Spring Boot 4.3 Upgrade
**Priority:** ⭐⭐⭐⭐⭐ (MOST CRITICAL)
**Status:** Not Started (Planned)
**Estimate:** 🔴 XL (21 pts) - 2-3 weeks (Full testing suite + Compatibility checks)
**Description:** Upgrade from Spring Boot 3.1.5 to 4.3 for latest features, performance improvements, and Java 21+ support.
**Rationale:**
- Modern LTS version with extended support
- Better performance optimizations
- Improved cloud-native features
- Latest Spring Security enhancements

### PostgreSQL JSONB Hybrid Architecture
**Priority:** ⭐⭐⭐⭐
**Status:** Not Started (Planned for v2.1)
**Estimate:** 🔴 XL (21 pts) - 2-3 weeks (Incremental migration)
**Description:** Leverage PostgreSQL JSONB for flexible MCDM model storage while maintaining relational structure for ownership/audit.
**Approach:**
- Relational Layer: projects, users, audit_log (immutable)
- JSONB Layer: prioritisations.structure (flexible method-specific schemas)
- No schema migration for new MCDM methods
- JSONB Indexing for efficient queries

**Benefits:**
- Add new MCDM methods without table migrations
- Flexible data structures per solving method
- Native PostgreSQL JSON operations
- Maintains strong consistency for audit trail

### Migration Away from Hibernate (Optional - Spring Data JPA focused)
**Priority:** ⭐⭐⭐
**Status:** Not Started (Post-JSONB Architecture)
**Estimate:** 🔴 XL (21+ pts) - 2+ weeks (Per-module migration)
**Description:** Consider lightweight persistence layer (JOOQ, MyBatis) after JSONB architecture stabilizes.
**Current Decision:** Stick with Spring Data JPA + JSONB (good middle ground)
- Spring Data JPA handles relational CRUD well
- JSONB reduces ORM complexity for flexible structures
- Envers audit logging can be replaced with PostgreSQL event sourcing later

---



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

## 📊 Estimation Summary

### By Priority Level

| Level | Features | Effort | Timeline |
|-------|----------|--------|----------|
| ⭐⭐⭐⭐⭐ CRITICAL | 10 items (5 done, 5 pending) | ~60 pts | 6-8 weeks |
| ⭐⭐⭐⭐ HIGH | 15 items (1 done, 14 pending) | ~110 pts | 12-14 weeks |
| ⭐⭐⭐ MEDIUM | 15 items (1 done, 14 pending) | ~80 pts | 8-10 weeks |
| ⭐⭐ LOW | 4 items | ~30 pts | 3-4 weeks |
| ⭐ NICE-TO-HAVE | 1 item | ~5 pts | < 1 week |

**TOTAL:** ~285 Story Points ≈ **28-32 weeks** (7-8 months) for all pending features

### Quick Wins (Sprint Ready)

| Feature | Estimate | Effort |
|---------|----------|--------|
| Dark Mode | 🟢 S (3 pts) | 1 day |
| WSM/SAW | 🟢 S (3 pts) | 1 day |
| SMART | 🟢 S (3 pts) | 1 day |
| Swagger API Docs | 🟢 S (3 pts) | 1 day |
| Account Lockout | 🟡 M (5 pts) | 1-2 days |
| Multi-Criteria Sorting | 🟡 M (5 pts) | 1-2 days |

**Quick Wins Total:** 🟢 ~23 pts = **1-2 weeks** for easy momentum

### High-Impact Features (Next Priority)

1. **TOPSIS** (🟠 L, 13 pts) - Complements AHP well
2. **BWM** (🟠 L, 13 pts) - Simpler alternative to AHP
3. **PostgreSQL JSONB Architecture** (🔴 XL, 21 pts) - Foundation for flexibility
4. **PDF/Excel Export** (🟡 M, 8 pts) - User value
5. **OAuth 2.0 Social Login** (🟠 L, 13 pts) - User growth

**High-Impact First Wave:** ~68 pts = **6-8 weeks**

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

**Last Updated:** 11. Januar 2026 11:15
**Latest Session:** Architektur-Entscheidungen - Spring Boot 4.3 Upgrade + Migration away from Hibernate
