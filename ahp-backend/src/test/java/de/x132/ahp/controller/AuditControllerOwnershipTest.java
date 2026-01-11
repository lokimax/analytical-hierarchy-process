package de.x132.ahp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.NodeService;
import de.x132.ahp.service.ProjectService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuditControllerOwnershipTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ProjectService projectService;
  @Autowired private NodeService nodeService;
  @Autowired private AnalysisService analysisService;
  @Autowired private ClientRepository clientRepository;
  @Autowired private EntityManager entityManager;

  private Client ownerClient;
  private Client anotherClient;
  private Project ownedProject;
  private Project unownedProject;
  private Node ownedNode;
  private Analysis ownedAnalysis;

  @BeforeEach
  void setUp() {
    ownerClient =
        Client.builder()
            .nickname("owneruser")
            .name("Owner")
            .surename("User")
            .password("hashedpassword")
            .email("owner@test.com")
            .build();
    ownerClient = clientRepository.save(ownerClient);

    anotherClient =
        Client.builder()
            .nickname("anotheruser")
            .name("Another")
            .surename("User")
            .password("hashedpassword")
            .email("another@test.com")
            .build();
    anotherClient = clientRepository.save(anotherClient);

    ownedProject = Project.builder().name("OwnedProject").client(ownerClient).build();
    ownedProject = projectService.createProject(ownedProject);

    unownedProject = Project.builder().name("UnownedProject").client(anotherClient).build();
    unownedProject = projectService.createProject(unownedProject);

    ownedNode = Node.builder().name("OwnedNode").project(ownedProject).build();
    ownedNode = nodeService.createNode(ownedNode);

    ownedAnalysis = Analysis.builder().name("OwnedAnalysis").project(ownedProject).build();
    ownedAnalysis = analysisService.createAnalysis(ownedAnalysis);

    entityManager.flush();
  }

  @Test
  void testGetProjectRevisions_Forbidden_WhenUserDoesNotOwnProject() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/projects/{projectId}/revisions", unownedProject.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetProjectHistory_Forbidden_WhenUserDoesNotOwnProject() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/projects/{projectId}/history", unownedProject.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetProjectRevisions_Allowed_WhenUserOwnsProject() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/projects/{projectId}/revisions", ownedProject.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetProjectHistory_Allowed_WhenUserOwnsProject() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/projects/{projectId}/history", ownedProject.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetNodeRevisions_Forbidden_WhenUserDoesNotOwnNode() throws Exception {
    Node unownedNode = Node.builder().name("UnownedNode").project(unownedProject).build();
    unownedNode = nodeService.createNode(unownedNode);
    entityManager.flush();

    mockMvc
        .perform(
            get("/api/audit/nodes/{nodeId}/revisions", unownedNode.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetNodeHistory_Forbidden_WhenUserDoesNotOwnNode() throws Exception {
    Node unownedNode = Node.builder().name("UnownedNode").project(unownedProject).build();
    unownedNode = nodeService.createNode(unownedNode);
    entityManager.flush();

    mockMvc
        .perform(
            get("/api/audit/nodes/{nodeId}/history", unownedNode.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetNodeRevisions_Allowed_WhenUserOwnsNode() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/nodes/{nodeId}/revisions", ownedNode.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetNodeHistory_Allowed_WhenUserOwnsNode() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/nodes/{nodeId}/history", ownedNode.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAnalysisRevisions_Forbidden_WhenUserDoesNotOwnAnalysis() throws Exception {
    Analysis unownedAnalysis =
        Analysis.builder().name("UnownedAnalysis").project(unownedProject).build();
    unownedAnalysis = analysisService.createAnalysis(unownedAnalysis);
    entityManager.flush();

    mockMvc
        .perform(
            get("/api/audit/analyses/{analysisId}/revisions", unownedAnalysis.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetAnalysisHistory_Forbidden_WhenUserDoesNotOwnAnalysis() throws Exception {
    Analysis unownedAnalysis =
        Analysis.builder().name("UnownedAnalysis").project(unownedProject).build();
    unownedAnalysis = analysisService.createAnalysis(unownedAnalysis);
    entityManager.flush();

    mockMvc
        .perform(
            get("/api/audit/analyses/{analysisId}/history", unownedAnalysis.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetAnalysisRevisions_Allowed_WhenUserOwnsAnalysis() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/analyses/{analysisId}/revisions", ownedAnalysis.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetAnalysisHistory_Allowed_WhenUserOwnsAnalysis() throws Exception {
    mockMvc
        .perform(
            get("/api/audit/analyses/{analysisId}/history", ownedAnalysis.getId())
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }

  @Test
  void testGetEntityAtRevision_Forbidden_WhenUserDoesNotOwnProject() throws Exception {
    mockMvc
        .perform(
            get(
                    "/api/audit/{entityType}/{entityId}/revision/{revisionNumber}",
                    "project",
                    unownedProject.getId(),
                    1)
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetEntityAtRevision_Allowed_WhenUserOwnsProject() throws Exception {
    mockMvc
        .perform(
            get(
                    "/api/audit/{entityType}/{entityId}/revision/{revisionNumber}",
                    "project",
                    ownedProject.getId(),
                    1)
                .with(httpBasic("owneruser", "password")))
        .andExpect(status().isOk());
  }
}
