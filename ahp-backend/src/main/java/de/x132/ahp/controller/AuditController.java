package de.x132.ahp.controller;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Comparison;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.security.SecurityUtils;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.AuditService;
import de.x132.ahp.service.NodeService;
import de.x132.ahp.service.ProjectService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

  private final AuditService auditService;
  private final ProjectService projectService;
  private final NodeService nodeService;
  private final AnalysisService analysisService;
  private final SecurityUtils securityUtils;

  public AuditController(
      AuditService auditService,
      ProjectService projectService,
      NodeService nodeService,
      AnalysisService analysisService,
      SecurityUtils securityUtils) {
    this.auditService = auditService;
    this.projectService = projectService;
    this.nodeService = nodeService;
    this.analysisService = analysisService;
    this.securityUtils = securityUtils;
  }

  /** Get all revisions for a Project */
  @GetMapping("/projects/{projectId}/revisions")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getProjectRevisions(
      @PathVariable Long projectId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!projectService.isOwner(projectId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> revisions = auditService.getEntityRevisions(Project.class, projectId);
    return ResponseEntity.ok(revisions);
  }

  /** Get complete history with changes for a Project */
  @GetMapping("/projects/{projectId}/history")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getProjectHistory(@PathVariable Long projectId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!projectService.isOwner(projectId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> history = auditService.getEntityHistory(Project.class, projectId);
    return ResponseEntity.ok(history);
  }

  /** Get all revisions for a Node */
  @GetMapping("/nodes/{nodeId}/revisions")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getNodeRevisions(@PathVariable Long nodeId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!nodeService.isOwner(nodeId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> revisions = auditService.getEntityRevisions(Node.class, nodeId);
    return ResponseEntity.ok(revisions);
  }

  /** Get complete history for a Node */
  @GetMapping("/nodes/{nodeId}/history")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getNodeHistory(@PathVariable Long nodeId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!nodeService.isOwner(nodeId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> history = auditService.getEntityHistory(Node.class, nodeId);
    return ResponseEntity.ok(history);
  }

  /** Get all revisions for an Analysis */
  @GetMapping("/analyses/{analysisId}/revisions")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getAnalysisRevisions(
      @PathVariable Long analysisId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!analysisService.isOwner(analysisId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> revisions =
        auditService.getEntityRevisions(Analysis.class, analysisId);
    return ResponseEntity.ok(revisions);
  }

  /** Get complete history for an Analysis */
  @GetMapping("/analyses/{analysisId}/history")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getAnalysisHistory(
      @PathVariable Long analysisId) {
    Client currentUser = securityUtils.getCurrentUser();

    if (!analysisService.isOwner(analysisId, currentUser)) {
      return ResponseEntity.status(403).build();
    }

    List<Map<String, Object>> history = auditService.getEntityHistory(Analysis.class, analysisId);
    return ResponseEntity.ok(history);
  }

  /** Get all revisions for a Comparison */
  @GetMapping("/comparisons/{comparisonId}/revisions")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getComparisonRevisions(
      @PathVariable Long comparisonId) {
    List<Map<String, Object>> revisions =
        auditService.getEntityRevisions(Comparison.class, comparisonId);
    return ResponseEntity.ok(revisions);
  }

  /** Get complete history for a Comparison */
  @GetMapping("/comparisons/{comparisonId}/history")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getComparisonHistory(
      @PathVariable Long comparisonId) {
    List<Map<String, Object>> history =
        auditService.getEntityHistory(Comparison.class, comparisonId);
    return ResponseEntity.ok(history);
  }

  /** Get all revisions for a Client (admin only) */
  @GetMapping("/clients/{clientId}/revisions")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, Object>>> getClientRevisions(@PathVariable Long clientId) {
    List<Map<String, Object>> revisions = auditService.getEntityRevisions(Client.class, clientId);
    return ResponseEntity.ok(revisions);
  }

  /** Get complete history for a Client (admin only) */
  @GetMapping("/clients/{clientId}/history")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, Object>>> getClientHistory(@PathVariable Long clientId) {
    List<Map<String, Object>> history = auditService.getEntityHistory(Client.class, clientId);
    return ResponseEntity.ok(history);
  }

  /** Get recent changes for all Projects */
  @GetMapping("/projects/recent")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getRecentProjectChanges(
      @RequestParam(defaultValue = "50") int limit) {
    limit = validateLimit(limit);
    List<Map<String, Object>> changes = auditService.getAllChanges(Project.class, limit);
    return ResponseEntity.ok(changes);
  }

  /** Get recent changes for all Nodes */
  @GetMapping("/nodes/recent")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getRecentNodeChanges(
      @RequestParam(defaultValue = "50") int limit) {
    limit = validateLimit(limit);
    List<Map<String, Object>> changes = auditService.getAllChanges(Node.class, limit);
    return ResponseEntity.ok(changes);
  }

  /** Get recent changes for all Analyses */
  @GetMapping("/analyses/recent")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<Map<String, Object>>> getRecentAnalysisChanges(
      @RequestParam(defaultValue = "50") int limit) {
    limit = validateLimit(limit);
    List<Map<String, Object>> changes = auditService.getAllChanges(Analysis.class, limit);
    return ResponseEntity.ok(changes);
  }

  /** Get entity at a specific revision */
  @GetMapping("/{entityType}/{entityId}/revision/{revisionNumber}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Object> getEntityAtRevision(
      @PathVariable String entityType,
      @PathVariable Long entityId,
      @PathVariable Number revisionNumber) {

    Class<?> entityClass = getEntityClass(entityType);
    if (entityClass == null) {
      return ResponseEntity.badRequest().build();
    }

    Client currentUser = securityUtils.getCurrentUser();

    boolean ownershipValid =
        switch (entityType.toLowerCase()) {
          case "project" -> projectService.isOwner(entityId, currentUser);
          case "node" -> nodeService.isOwner(entityId, currentUser);
          case "analysis" -> analysisService.isOwner(entityId, currentUser);
          case "client" -> entityId != null && entityId.equals(currentUser.getId());
          default -> false;
        };

    if (!ownershipValid) {
      return ResponseEntity.status(403).build();
    }

    Object entity = auditService.findEntityAtRevision(entityClass, entityId, revisionNumber);
    if (entity == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(entity);
  }

  private Class<?> getEntityClass(String entityType) {
    return switch (entityType.toLowerCase()) {
      case "project" -> Project.class;
      case "node" -> Node.class;
      case "analysis" -> Analysis.class;
      case "comparison" -> Comparison.class;
      case "client" -> Client.class;
      default -> null;
    };
  }

  private int validateLimit(int limit) {
    if (limit < 1) {
      return 1;
    }
    if (limit > 1000) {
      return 1000;
    }
    return limit;
  }
}
