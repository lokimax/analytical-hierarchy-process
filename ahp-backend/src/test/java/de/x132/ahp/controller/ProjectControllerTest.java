package de.x132.ahp.controller;

import tools.jackson.databind.ObjectMapper;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.ProjectRepository;
import de.x132.ahp.repository.TokenRepository;
import de.x132.ahp.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController.
 * Tests project creation, retrieval, update, and deletion.
 *
 * @author Max Wick
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProjectControllerTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    private Client testClient;
    private String testPassword = "password123";
    private String authToken;

    @BeforeEach
    public void setup() {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Clean up
        projectRepository.deleteAll();
        tokenRepository.deleteAll();
        clientRepository.deleteAll();

        // Create test client
        testClient = Client.builder()
                .nickname("testuser")
                .name("Test")
                .surename("User")
                .email("test@example.com")
                .password(passwordEncoder.encode(testPassword))
                .status(UserStatus.ACTIVE)
                .build();
        testClient = clientRepository.save(testClient);

        // Login to get token
        authToken = authenticationService.login("testuser", testPassword);
    }

    @Test
    public void testCreateProject() throws Exception {
        Project newProject = Project.builder()
                .name("Test Project")
                .beschreibung("A test project for AHP")
                .build();

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.beschreibung").value("A test project for AHP"));
    }

    @Test
    public void testCreateProjectWithoutAuthentication() throws Exception {
        Project newProject = Project.builder()
                .name("Test Project")
                .beschreibung("Should fail")
                .build();

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllProjects() throws Exception {
        // Create test projects
        Project project1 = Project.builder()
                .name("Project 1")
                .beschreibung("First project")
                .client(testClient)
                .build();
        projectRepository.save(project1);

        Project project2 = Project.builder()
                .name("Project 2")
                .beschreibung("Second project")
                .client(testClient)
                .build();
        projectRepository.save(project2);

        mockMvc.perform(get("/api/projects")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Project 1"))
                .andExpect(jsonPath("$[1].name").value("Project 2"));
    }

    @Test
    public void testGetProjectByName() throws Exception {
        Project project = Project.builder()
                .name("TestProject")
                .beschreibung("A specific project")
                .client(testClient)
                .build();
        projectRepository.save(project);

        mockMvc.perform(get("/api/projects/TestProject")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProject"))
                .andExpect(jsonPath("$.beschreibung").value("A specific project"));
    }

    @Test
    public void testGetNonexistentProject() throws Exception {
        mockMvc.perform(get("/api/projects/NonexistentProject")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateProject() throws Exception {
        Project project = Project.builder()
                .name("OriginalName")
                .beschreibung("Original description")
                .client(testClient)
                .build();
        project = projectRepository.save(project);

        project.setBeschreibung("Updated description");

        mockMvc.perform(put("/api/projects/" + project.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beschreibung").value("Updated description"));
    }

    @Test
    public void testDeleteProject() throws Exception {
        Project project = Project.builder()
                .name("ProjectToDelete")
                .beschreibung("Will be deleted")
                .client(testClient)
                .build();
        project = projectRepository.save(project);
        Long projectId = project.getId();

        mockMvc.perform(delete("/api/projects/" + projectId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Verify project is deleted
        mockMvc.perform(get("/api/projects/ProjectToDelete")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateProjectRoundTrip() throws Exception {
        // Create project
        Project newProject = Project.builder()
                .name("RoundTripProject")
                .beschreibung("Testing complete lifecycle")
                .build();

        String response = mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Project createdProject = objectMapper.readValue(response, Project.class);

        // Get project by name
        mockMvc.perform(get("/api/projects/" + createdProject.getName())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("RoundTripProject"));

        // Update project
        createdProject.setBeschreibung("Updated in round trip");
        mockMvc.perform(put("/api/projects/" + createdProject.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beschreibung").value("Updated in round trip"));

        // Delete project
        mockMvc.perform(delete("/api/projects/" + createdProject.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Verify deletion
        mockMvc.perform(get("/api/projects/" + createdProject.getName())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUserCanOnlyAccessOwnProjects() throws Exception {
        // Create another user
        Client otherClient = Client.builder()
                .nickname("otheruser")
                .name("Other")
                .surename("User")
                .email("other@example.com")
                .password(passwordEncoder.encode("otherpass"))
                .status(UserStatus.ACTIVE)
                .build();
        otherClient = clientRepository.save(otherClient);

        // Create project for other user
        Project otherProject = Project.builder()
                .name("OtherProject")
                .beschreibung("Belongs to other user")
                .client(otherClient)
                .build();
        projectRepository.save(otherProject);

        // testClient should not see other user's project in their list
        mockMvc.perform(get("/api/projects")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Should have no projects
    }
}
