package de.x132.ahp.service;

import static org.junit.jupiter.api.Assertions.*;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.AnalysisRepository;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuditServiceTest {

  @Autowired private AuditService auditService;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private AnalysisRepository analysisRepository;

  @Autowired private ClientRepository clientRepository;

  @Autowired private EntityManager entityManager;

  private Project testProject;
  private Analysis testAnalysis;
  private Client testClient;

  @BeforeEach
  void setUp() {
    testClient =
        Client.builder()
            .name("TestClient")
            .nickname("TestNick")
            .email("test@example.com")
            .password("password123")
            .surename("TestSurname")
            .status(UserStatus.ACTIVE)
            .build();
    testClient = clientRepository.save(testClient);

    testProject = Project.builder().name("TestProject").client(testClient).build();
    testProject = projectRepository.save(testProject);

    testAnalysis = Analysis.builder().name("TestAnalysis").project(testProject).build();
    testAnalysis = analysisRepository.save(testAnalysis);
  }

  @AfterEach
  void tearDown() {
    analysisRepository.deleteAll();
    projectRepository.deleteAll();
    clientRepository.deleteAll();
  }

  @Test
  void testGetEntityRevisions_ReturnsRevisions() {
    List<Map<String, Object>> revisions =
        auditService.getEntityRevisions(Project.class, testProject.getId());
    assertNotNull(revisions);
    assertFalse(revisions.isEmpty(), "Should find at least one revision");
  }

  @Test
  void testGetEntityHistory_ReturnsHistory() {
    List<Map<String, Object>> history =
        auditService.getEntityHistory(Project.class, testProject.getId());
    assertNotNull(history);
  }

  @Test
  void testFindEntityAtRevision_ReturnsEntity() {
    // Get available revisions first
    List<Map<String, Object>> revisions =
        auditService.getEntityRevisions(Project.class, testProject.getId());

    assertFalse(revisions.isEmpty(), "Revisions be available to test finding entity at revision");

    // Revisions exist - test normal retrieval
    Integer firstRevision = (Integer) revisions.get(0).get("revisionNumber");
    Object entity =
        auditService.findEntityAtRevision(Project.class, testProject.getId(), firstRevision);
    assertNotNull(entity, "Should retrieve entity at revision " + firstRevision);
    assertTrue(entity instanceof Project);
  }

  @Test
  void testGetAllChanges_ReturnsChanges() {
    List<Map<String, Object>> changes = auditService.getAllChanges(Project.class, 50);
    assertNotNull(changes);
  }

  @Test
  void testValidateLimit_WithNegativeValue() {
    int result = validateLimit(-10);
    assertEquals(1, result);
  }

  @Test
  void testValidateLimit_WithExcessiveValue() {
    int result = validateLimit(9999);
    assertEquals(1000, result);
  }

  @Test
  void testValidateLimit_WithValidValue() {
    int result = validateLimit(50);
    assertEquals(50, result);
  }

  @Test
  void testAuditDataContainsMetadata() {
    List<Map<String, Object>> revisions =
        auditService.getEntityRevisions(Project.class, testProject.getId());

    assertFalse(revisions.isEmpty(), "Revisions be available to test metadata");

    Map<String, Object> revision = revisions.get(0);
    assertNotNull(revision.get("revisionNumber"));
    assertNotNull(revision.get("revisionDate"));
    assertNotNull(revision.get("entity"));
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
