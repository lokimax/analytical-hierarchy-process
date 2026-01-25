package de.x132.ahp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.json.AnalysisResult;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.ProjectService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AnalysisController Integration Tests")
class AnalysisControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AnalysisService analysisService;

  @MockBean private ProjectService projectService;

  @MockBean private AuthenticationService authenticationService;

  private Client testClient;
  private Project testProject;
  private Analysis testAnalysis;
  private UsernamePasswordAuthenticationToken authentication;

  @BeforeEach
  void setUp() {
    testClient = Client.builder().id(1L).nickname("testuser").email("test@example.com").build();

    testProject =
        Project.builder()
            .id(1L)
            .name("TestProject")
            .beschreibung("Test Description")
            .client(testClient)
            .build();

    testAnalysis =
        Analysis.builder()
            .id(1L)
            .name("Test Analysis")
            .beschreibung("Test Analysis Description")
            .project(testProject)
            .criteriaComparisons("{}")
            .alternativeComparisons("{}")
            .results(AnalysisResult.builder().build())
            .createdAt(LocalDateTime.now())
            .build();

    authentication = new UsernamePasswordAuthenticationToken(testClient, null);
  }

  @Test
  @WithMockUser
  @DisplayName("POST /api/projects/{projectName}/analyses - Should create analysis successfully")
  void shouldCreateAnalysisSuccessfully() throws Exception {
    // Given
    AnalysisRequest request = new AnalysisRequest();
    request.setName("New Analysis");
    request.setBeschreibung("Description");
    request.setCriteriaComparisons("{}");
    request.setAlternativeComparisons("{}");
    request.setResults("{}");

    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findByProjectAndName(testProject, "New Analysis"))
        .thenReturn(Optional.empty());
    when(analysisService.createAnalysis(any(Analysis.class))).thenReturn(testAnalysis);

    // When & Then
    mockMvc
        .perform(
            post("/api/projects/TestProject/analyses")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Analysis"));

    verify(analysisService, times(1)).createAnalysis(any(Analysis.class));
  }

  @Test
  @WithMockUser
  @DisplayName(
      "POST /api/projects/{projectName}/analyses - Should return 404 when project not found")
  void shouldReturn404WhenProjectNotFound() throws Exception {
    // Given
    AnalysisRequest request = new AnalysisRequest();
    request.setName("New Analysis");

    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "NonexistentProject"))
        .thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            post("/api/projects/NonexistentProject/analyses")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  @DisplayName(
      "POST /api/projects/{projectName}/analyses - Should return 400 when analysis name already exists")
  void shouldReturn400WhenAnalysisNameExists() throws Exception {
    // Given
    AnalysisRequest request = new AnalysisRequest();
    request.setName("Existing Analysis");

    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findByProjectAndName(testProject, "Existing Analysis"))
        .thenReturn(Optional.of(testAnalysis));

    // When & Then
    mockMvc
        .perform(
            post("/api/projects/TestProject/analyses")
                .with(authentication(authentication))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/projects/{projectName}/analyses - Should return all analyses")
  void shouldReturnAllAnalyses() throws Exception {
    // Given
    Analysis analysis2 = Analysis.builder().id(2L).name("Analysis 2").project(testProject).build();

    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findAllByProject(testProject))
        .thenReturn(Arrays.asList(testAnalysis, analysis2));

    // When & Then
    mockMvc
        .perform(get("/api/projects/TestProject/analyses").with(authentication(authentication)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Test Analysis"))
        .andExpect(jsonPath("$[1].name").value("Analysis 2"));
  }

  @Test
  @WithMockUser
  @DisplayName(
      "GET /api/projects/{projectName}/analyses/{analysisId} - Should return analysis by id")
  void shouldReturnAnalysisById() throws Exception {
    // Given
    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findById(1L)).thenReturn(Optional.of(testAnalysis));

    // When & Then
    mockMvc
        .perform(get("/api/projects/TestProject/analyses/1").with(authentication(authentication)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Analysis"));
  }

  @Test
  @WithMockUser
  @DisplayName(
      "GET /api/projects/{projectName}/analyses/{analysisId} - Should return 404 when analysis not found")
  void shouldReturn404WhenAnalysisNotFound() throws Exception {
    // Given
    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/api/projects/TestProject/analyses/999").with(authentication(authentication)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  @DisplayName(
      "DELETE /api/projects/{projectName}/analyses/{analysisId} - Should delete analysis successfully")
  void shouldDeleteAnalysisSuccessfully() throws Exception {
    // Given
    when(authenticationService.getAuthenticatedClient(any())).thenReturn(testClient);
    when(projectService.findByClientNicknameAndName("testuser", "TestProject"))
        .thenReturn(Optional.of(testProject));
    when(analysisService.findById(1L)).thenReturn(Optional.of(testAnalysis));
    doNothing().when(analysisService).deleteAnalysis(1L);

    // When & Then
    mockMvc
        .perform(
            delete("/api/projects/TestProject/analyses/1").with(authentication(authentication)))
        .andExpect(status().isNoContent());

    verify(analysisService, times(1)).deleteAnalysis(1L);
  }
}
