package de.x132.ahp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditService {

  @PersistenceContext private EntityManager entityManager;

  /** Get all revisions for a specific entity */
  public List<Map<String, Object>> getEntityRevisions(Class<?> entityClass, Object entityId) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);

    List<Number> revisions = auditReader.getRevisions(entityClass, entityId);
    List<Map<String, Object>> result = new ArrayList<>();

    for (Number revision : revisions) {
      Object entity = auditReader.find(entityClass, entityId, revision);
      Date revisionDate = auditReader.getRevisionDate(revision);

      Map<String, Object> revisionInfo = new HashMap<>();
      revisionInfo.put("revisionNumber", revision);
      revisionInfo.put("revisionDate", revisionDate);
      revisionInfo.put("entity", entity);

      result.add(revisionInfo);
    }

    return result;
  }

  /** Get revision history with changes for an entity */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getEntityHistory(Class<?> entityClass, Object entityId) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);

    AuditQuery query =
        auditReader
            .createQuery()
            .forRevisionsOfEntity(entityClass, false, true)
            .add(AuditEntity.id().eq(entityId));

    List<Object[]> results = query.getResultList();
    List<Map<String, Object>> history = new ArrayList<>();

    for (Object[] result : results) {
      Object entity = result[0];
      org.hibernate.envers.DefaultRevisionEntity revisionEntity =
          (org.hibernate.envers.DefaultRevisionEntity) result[1];
      RevisionType revisionType = (RevisionType) result[2];

      Map<String, Object> historyEntry = new HashMap<>();
      historyEntry.put("entity", entity);
      historyEntry.put("revisionNumber", revisionEntity.getId());
      historyEntry.put("revisionDate", new Date(revisionEntity.getTimestamp()));
      historyEntry.put("revisionType", revisionType.name());

      history.add(historyEntry);
    }

    return history;
  }

  /** Get all changes for a specific entity type */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getAllChanges(Class<?> entityClass, int maxResults) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);

    AuditQuery query =
        auditReader
            .createQuery()
            .forRevisionsOfEntity(entityClass, false, true)
            .addOrder(AuditEntity.revisionNumber().desc())
            .setMaxResults(maxResults);

    List<Object[]> results = query.getResultList();
    List<Map<String, Object>> changes = new ArrayList<>();

    for (Object[] result : results) {
      Object entity = result[0];
      org.hibernate.envers.DefaultRevisionEntity revisionEntity =
          (org.hibernate.envers.DefaultRevisionEntity) result[1];
      RevisionType revisionType = (RevisionType) result[2];

      Map<String, Object> change = new HashMap<>();
      change.put("entity", entity);
      change.put("revisionNumber", revisionEntity.getId());
      change.put("revisionDate", new Date(revisionEntity.getTimestamp()));
      change.put("revisionType", revisionType.name());

      changes.add(change);
    }

    return changes;
  }

  /** Find entity at a specific revision */
  public Object findEntityAtRevision(Class<?> entityClass, Object entityId, Number revisionNumber) {
    AuditReader auditReader = AuditReaderFactory.get(entityManager);
    return auditReader.find(entityClass, entityId, revisionNumber);
  }
}
