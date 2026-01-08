package de.x132.ahp.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.x132.ahp.dto.LoginRequest;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
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
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for ClientController (Authentication and User Management). Tests user
 * registration, login, logout, and profile access.
 *
 * @author Max Wick
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientControllerTest {

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ClientRepository clientRepository;

  @Autowired private TokenRepository tokenRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private AuthenticationService authenticationService;

  private Client testClient;
  private String testPassword = "password123";

  @BeforeEach
  public void setup() {
    // Setup MockMvc with Spring Security
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

    // Clean up tokens and clients
    tokenRepository.deleteAll();
    clientRepository.deleteAll();

    // Create a test client
    testClient =
        Client.builder()
            .nickname("testuser")
            .name("Test")
            .surename("User")
            .email("test@example.com")
            .password(passwordEncoder.encode(testPassword))
            .status(UserStatus.ACTIVE)
            .build();
    testClient = clientRepository.save(testClient);
  }

  @Test
  public void testRegisterNewUser() throws Exception {
    Client newClient =
        Client.builder()
            .nickname("newuser")
            .name("New")
            .surename("User")
            .email("newuser@example.com")
            .password("newpassword123")
            .build();

    mockMvc
        .perform(
            post("/api/clients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newClient)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nickname").value("newuser"))
        .andExpect(jsonPath("$.email").value("newuser@example.com"))
        .andExpect(jsonPath("$.status").value("PENDING_ACTIVATION"));
  }

  @Test
  public void testRegisterDuplicateNickname() throws Exception {
    Client duplicateClient =
        Client.builder()
            .nickname("testuser") // Already exists
            .name("Duplicate")
            .surename("User")
            .email("duplicate@example.com")
            .password("password")
            .build();

    mockMvc
        .perform(
            post("/api/clients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateClient)))
        .andExpect(status().isConflict());
  }

  @Test
  public void testLoginWithValidCredentials() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setNickname("testuser");
    loginRequest.setPassword(testPassword);

    mockMvc
        .perform(
            post("/api/clients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.token", hasLength(32)))
        .andExpect(jsonPath("$.nickname").value("testuser"));
  }

  @Test
  public void testLoginWithInvalidPassword() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setNickname("testuser");
    loginRequest.setPassword("wrongpassword");

    mockMvc
        .perform(
            post("/api/clients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testLoginWithNonexistentUser() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setNickname("nonexistent");
    loginRequest.setPassword("password");

    mockMvc
        .perform(
            post("/api/clients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testGetUserProfile() throws Exception {
    // Login to get token
    String token = authenticationService.login("testuser", testPassword);

    mockMvc
        .perform(get("/api/clients/testuser").header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value("testuser"))
        .andExpect(jsonPath("$.name").value("Test"))
        .andExpect(jsonPath("$.surename").value("User"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  public void testGetUserProfileWithoutToken() throws Exception {
    mockMvc.perform(get("/api/clients/testuser")).andExpect(status().isForbidden());
  }

  @Test
  public void testGetUserProfileWithInvalidToken() throws Exception {
    mockMvc
        .perform(get("/api/clients/testuser").header("Authorization", "Bearer invalidtoken"))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testLogout() throws Exception {
    // Login to get token
    String token = authenticationService.login("testuser", testPassword);

    // Logout
    mockMvc
        .perform(delete("/api/clients/logout").header("X-Auth-Token", token))
        .andExpect(status().isOk());

    // Try to access profile with the logged out token (should fail)
    mockMvc
        .perform(get("/api/clients/testuser").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  public void testCompleteUserLifecycle() throws Exception {
    // 1. Register new user
    Client newUser =
        Client.builder()
            .nickname("lifecycleuser")
            .name("Lifecycle")
            .surename("Test")
            .email("lifecycle@example.com")
            .password("lifecyclepass")
            .build();

    mockMvc
        .perform(
            post("/api/clients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
        .andExpect(status().isCreated());

    // 2. Activate user
    Client userToActivate =
        clientRepository
            .findByNickname("lifecycleuser")
            .orElseThrow(() -> new RuntimeException("User not found"));
    userToActivate.setStatus(UserStatus.ACTIVE);
    clientRepository.save(userToActivate);

    // 3. Login
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setNickname("lifecycleuser");
    loginRequest.setPassword("lifecyclepass");

    String response =
        mockMvc
            .perform(
                post("/api/clients/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(response).get("token").asText();

    // 4. Access profile
    mockMvc
        .perform(get("/api/clients/lifecycleuser").header("X-Auth-Token", token))
        .andExpect(status().isOk());

    // 5. Logout
    mockMvc
        .perform(delete("/api/clients/logout").header("X-Auth-Token", token))
        .andExpect(status().isOk());

    // 6. Try to access after logout (should fail)
    mockMvc
        .perform(get("/api/clients/lifecycleuser").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());

    // 7. Login again (should work)
    mockMvc
        .perform(
            post("/api/clients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk());
  }
}
