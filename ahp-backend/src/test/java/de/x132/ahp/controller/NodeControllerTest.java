package de.x132.ahp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.x132.ahp.dto.NodeRequest;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.ConnectionRepository;
import de.x132.ahp.repository.NodeRepository;
import de.x132.ahp.repository.ProjectRepository;
import de.x132.ahp.repository.TokenRepository;
import de.x132.ahp.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for NodeController. Tests node and connection creation, retrieval, update, and
 * deletion.
 *
 * @author Max Wick
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NodeControllerTest {

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ClientRepository clientRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private NodeRepository nodeRepository;

  @Autowired private ConnectionRepository connectionRepository;

  @Autowired private TokenRepository tokenRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private AuthenticationService authenticationService;

  private Client testClient;
  private Project testProject;
  private String authToken;

  @BeforeEach
  public void setup() {
    // Setup MockMvc with Spring Security
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    // Clean up
    connectionRepository.deleteAll();
    nodeRepository.deleteAll();
    projectRepository.deleteAll();
    tokenRepository.deleteAll();
    clientRepository.deleteAll();

    // Create test client
    testClient =
        Client.builder()
            .nickname("testuser")
            .name("Test")
            .surename("User")
            .email("test@example.com")
            .password(passwordEncoder.encode("password123"))
            .status(UserStatus.ACTIVE)
            .build();
    testClient = clientRepository.save(testClient);

    // Create test project
    testProject =
        Project.builder()
            .name("TestProject")
            .beschreibung("Project for node testing")
            .client(testClient)
            .build();
    testProject = projectRepository.save(testProject);

    // Login to get token
    authToken = authenticationService.login("testuser", "password123");
  }

  @Test
  @WithMockUser(username = "testuser")
  public void testCreateNode() throws Exception {
    NodeRequest nodeRequest = new NodeRequest();
    nodeRequest.setName("TestNode");
    nodeRequest.setBeschreibung("A test node");
    nodeRequest.setContent("Node content");

    mockMvc
        .perform(
            post("/api/projects/TestProject/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("TestNode"))
        .andExpect(jsonPath("$.beschreibung").value("A test node"));
  }

  @Test
  public void testCreateMultipleNodes() throws Exception {
    String[] nodeNames = {"Goal", "Criterion1", "Criterion2", "Alternative1"};

    for (String nodeName : nodeNames) {
      NodeRequest nodeRequest = new NodeRequest();
      nodeRequest.setName(nodeName);
      nodeRequest.setBeschreibung("Description for " + nodeName);

      mockMvc
          .perform(
              post("/api/projects/TestProject/nodes")
                  .header("Authorization", "Bearer " + authToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(nodeRequest)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value(nodeName));
    }

    // Verify all nodes exist
    mockMvc
        .perform(
            get("/api/projects/TestProject/nodes").header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(4)));
  }

  @Test
  public void testGetAllNodes() throws Exception {
    // Create test nodes
    Node node1 = Node.builder().name("Node1").content("First node").project(testProject).build();
    nodeRepository.save(node1);

    Node node2 = Node.builder().name("Node2").content("Second node").project(testProject).build();
    nodeRepository.save(node2);

    mockMvc
        .perform(
            get("/api/projects/TestProject/nodes").header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Node1"))
        .andExpect(jsonPath("$[1].name").value("Node2"));
  }

  @Test
  public void testGetNodeByName() throws Exception {
    Node node =
        Node.builder()
            .name("SpecificNode")
            .content("Specific content")
            .beschreibung("Specific description")
            .project(testProject)
            .build();
    nodeRepository.save(node);

    mockMvc
        .perform(
            get("/api/projects/TestProject/nodes/SpecificNode")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("SpecificNode"))
        .andExpect(jsonPath("$.beschreibung").value("Specific description"));
  }

  @Test
  public void testDeleteNode() throws Exception {
    Node node =
        Node.builder().name("NodeToDelete").content("Will be deleted").project(testProject).build();
    nodeRepository.save(node);

    mockMvc
        .perform(
            delete("/api/projects/TestProject/nodes/NodeToDelete")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNoContent());

    // Verify node is deleted
    mockMvc
        .perform(
            get("/api/projects/TestProject/nodes/NodeToDelete")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testCreateConnection() throws Exception {
    // Create source and target nodes
    Node sourceNode = Node.builder().name("SourceNode").project(testProject).build();
    nodeRepository.save(sourceNode);

    Node targetNode = Node.builder().name("TargetNode").project(testProject).build();
    nodeRepository.save(targetNode);

    // Create connection request
    String connectionJson =
        String.format("{\"sourceNodeName\": \"SourceNode\", \"targetNodeName\": \"TargetNode\"}");

    mockMvc
        .perform(
            post("/api/projects/TestProject/connections")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(connectionJson))
        .andExpect(status().isOk());

    // Verify connection exists
    mockMvc
        .perform(
            get("/api/projects/TestProject/connections")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void testCompleteNetworkCreation() throws Exception {
    // Create network structure: Goal -> Criterion1, Criterion2, Criterion3
    // Each Criterion -> Alternative1, Alternative2

    // 1. Create nodes
    String[] nodeNames = {
      "Goal", "Criterion1", "Criterion2", "Criterion3", "Alternative1", "Alternative2"
    };

    for (String nodeName : nodeNames) {
      NodeRequest nodeRequest = new NodeRequest();
      nodeRequest.setName(nodeName);
      nodeRequest.setBeschreibung("Description for " + nodeName);

      mockMvc
          .perform(
              post("/api/projects/TestProject/nodes")
                  .header("Authorization", "Bearer " + authToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(nodeRequest)))
          .andExpect(status().isCreated());
    }

    // 2. Create connections: Goal to Criteria
    for (int i = 1; i <= 3; i++) {
      String connectionJson =
          String.format("{\"sourceNodeName\": \"Goal\", \"targetNodeName\": \"Criterion%d\"}", i);

      mockMvc
          .perform(
              post("/api/projects/TestProject/connections")
                  .header("Authorization", "Bearer " + authToken)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(connectionJson))
          .andExpect(status().isOk());
    }

    // 3. Create connections: Criteria to Alternatives
    for (int i = 1; i <= 3; i++) {
      for (int j = 1; j <= 2; j++) {
        String connectionJson =
            String.format(
                "{\"sourceNodeName\": \"Criterion%d\", \"targetNodeName\": \"Alternative%d\"}",
                i, j);

        mockMvc
            .perform(
                post("/api/projects/TestProject/connections")
                    .header("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(connectionJson))
            .andExpect(status().isOk());
      }
    }

    // 4. Verify all nodes exist
    mockMvc
        .perform(
            get("/api/projects/TestProject/nodes").header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(6)));

    // 5. Verify all connections exist (3 + 6 = 9 connections)
    mockMvc
        .perform(
            get("/api/projects/TestProject/connections")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(9)));
  }

  @Test
  public void testCreateNodeWithoutAuthentication() throws Exception {
    NodeRequest nodeRequest = new NodeRequest();
    nodeRequest.setName("UnauthorizedNode");

    mockMvc
        .perform(
            post("/api/projects/TestProject/nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testCreateDuplicateNode() throws Exception {
    Node existingNode = Node.builder().name("ExistingNode").project(testProject).build();
    nodeRepository.save(existingNode);

    NodeRequest duplicateRequest = new NodeRequest();
    duplicateRequest.setName("ExistingNode");

    mockMvc
        .perform(
            post("/api/projects/TestProject/nodes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  public void testDeleteConnection() throws Exception {
    // Create nodes and connection
    Node sourceNode = Node.builder().name("Source").project(testProject).build();
    sourceNode = nodeRepository.save(sourceNode);

    Node targetNode = Node.builder().name("Target").project(testProject).build();
    targetNode = nodeRepository.save(targetNode);

    String connectionJson = "{\"sourceNodeName\": \"Source\", \"targetNodeName\": \"Target\"}";

    mockMvc
        .perform(
            post("/api/projects/TestProject/connections")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(connectionJson))
        .andExpect(status().isOk());

    // Get connection ID from response
    String listResponse =
        mockMvc
            .perform(
                get("/api/projects/TestProject/connections")
                    .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Parse the first connection ID
    JsonNode jsonNode = objectMapper.readTree(listResponse);
    Long connectionId = jsonNode.get(0).get("id").asLong();

    // Delete connection
    mockMvc
        .perform(
            delete("/api/projects/TestProject/connections/" + connectionId)
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isNoContent());

    // Verify deletion
    mockMvc
        .perform(
            get("/api/projects/TestProject/connections")
                .header("Authorization", "Bearer " + authToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
