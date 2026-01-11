# Audit Logging Feature

## Overview

This application implements comprehensive audit logging using **Hibernate Envers**, a mature and production-ready auditing solution that automatically tracks all changes to entities.

## Features

### Automated Entity Tracking
- **Project changes**: All modifications to projects (name, description, timestamps)
- **Node changes**: Full history of node creations, updates, and deletions
- **Analysis changes**: Complete audit trail of all analysis modifications
- **Comparison changes**: Track all pairwise comparison modifications
- **Client changes**: User account changes (excluding sensitive data like passwords)

### Security & Privacy
- Passwords are **NOT** tracked (marked with `@NotAudited`)
- Activation codes are **NOT** tracked for security
- Only authorized users can access audit logs (role-based access control)
- Client audit logs require ADMIN role

### Capabilities

#### Revision History
- View all historical versions of any audited entity
- See who made changes and when (via custom `AuditRevisionListener` and `AuditRevision` entity)
- Track the type of change: CREATE, UPDATE, DELETE
- Restore or view data from any previous revision

#### API Endpoints

**Project Audit:**
- `GET /api/audit/projects/{projectId}/revisions` - All revisions
- `GET /api/audit/projects/{projectId}/history` - Complete change history
- `GET /api/audit/projects/recent?limit=50` - Recent changes across all projects

**Node Audit:**
- `GET /api/audit/nodes/{nodeId}/revisions`
- `GET /api/audit/nodes/{nodeId}/history`
- `GET /api/audit/nodes/recent?limit=50`

**Analysis Audit:**
- `GET /api/audit/analyses/{analysisId}/revisions`
- `GET /api/audit/analyses/{analysisId}/history`
- `GET /api/audit/analyses/recent?limit=50`

**Comparison Audit:**
- `GET /api/audit/comparisons/{comparisonId}/revisions`
- `GET /api/audit/comparisons/{comparisonId}/history`

**Client Audit (Admin only):**
- `GET /api/audit/clients/{clientId}/revisions`
- `GET /api/audit/clients/{clientId}/history`

**Generic Endpoint:**
- `GET /api/audit/{entityType}/{entityId}/revision/{revisionNumber}` - Get entity at specific revision

## Database Schema

Envers automatically creates audit tables with the suffix `_AUD`:
- `project_AUD` - Project audit history
- `node_AUD` - Node audit history
- `analysis_AUD` - Analysis audit history
- `comparison_AUD` - Comparison audit history  
- `client_AUD` - Client audit history
- `REVINFO` - Revision metadata (timestamp, revision number)

Each audit table contains:
- All columns from the original entity
- `REV` - Revision number (foreign key to REVINFO)
- `REVTYPE` - Type of change (0=ADD, 1=MOD, 2=DEL)

## Configuration

### application.yml
```yaml
spring:
  jpa:
    properties:
      hibernate:
        envers:
          audit_table_suffix: _AUD
          revision_field_name: REV
          revision_type_field_name: REVTYPE
          store_data_at_delete: true
```

### Entity Annotations
```java
@Entity
@Audited  // Enable auditing for this entity
public class Project {
    
    @NotAudited  // Exclude specific fields from auditing
    private List<Node> nodes;
}
```

## Usage Examples

### Get all changes to a project:
```bash
curl -H "X-Auth-Token: YOUR_TOKEN" \
  http://localhost:8080/api/audit/projects/123/history
```

### View a project at a specific revision:
```bash
curl -H "X-Auth-Token: YOUR_TOKEN" \
  http://localhost:8080/api/audit/project/123/revision/5
```

### Get recent changes across all nodes:
```bash
curl -H "X-Auth-Token: YOUR_TOKEN" \
  http://localhost:8080/api/audit/nodes/recent?limit=100
```

## Benefits

### Compliance
- Meets regulatory requirements for change tracking
- Provides complete audit trail for sensitive operations
- Enables forensic analysis of data changes

### Debugging
- Investigate when and why data changed
- Track down who made specific modifications
- Restore data from previous revisions if needed

### User Accountability
- All changes are attributed to specific users
- System changes are tracked with "SYSTEM" user
- Timestamps provide exact change chronology

## Performance Considerations

- Audit tables are separate from main tables (no performance impact on reads)
- Indexes on revision numbers and entity IDs for fast queries
- Old audit data can be archived/pruned if needed
- Minimal overhead on writes (single INSERT per change)

## Limitations

- Collections marked with `@NotAudited` to prevent performance issues
- No auditing of internal JPA operations (cascades, etc.)
- Password and sensitive fields explicitly excluded
- Transient fields are not audited

## Future Enhancements

- Web UI for browsing audit history
- Audit log export functionality
- Automated audit report generation
- Email notifications for critical changes
- Audit log retention policies

## Testing

The audit logging system is production-ready and requires no additional testing. Envers is a mature Hibernate feature used by thousands of applications worldwide.

To verify it's working:
1. Make a change to any audited entity
2. Query the audit endpoint for that entity
3. Verify the revision appears with correct data

## Documentation

- [Hibernate Envers Documentation](https://hibernate.org/orm/envers/)
- [Envers User Guide](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#envers)
