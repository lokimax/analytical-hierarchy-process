package de.x132.ahp.config;

/**
 * Configuration for Hibernate Envers audit logging. User tracking is handled through the custom
 * AuditRevisionListener which captures the authenticated user via Spring Security context.
 *
 * @author Max Wick
 */
public class AuditConfig {
  // Configuration is handled through:
  // 1. @Audited annotations on entities
  // 2. AuditRevision entity with @RevisionEntity annotation
  // 3. AuditRevisionListener for capturing user information
  // 4. Envers properties in application.yml
}
