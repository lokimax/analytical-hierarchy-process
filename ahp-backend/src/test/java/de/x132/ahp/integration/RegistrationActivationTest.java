package de.x132.ahp.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.x132.ahp.dto.AuthResponse;
import de.x132.ahp.dto.ClientRegistrationRequest;
import de.x132.ahp.dto.ClientResponse;
import de.x132.ahp.dto.LoginRequest;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Token;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.TokenRepository;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.ClientService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for user registration and account activation flow.
 *
 * <p>Tests: - User can register successfully - Registration generates an activation token - User
 * cannot log in before activation - User can activate their account with a valid token - User can
 * log in after activation - User cannot activate with invalid/expired token
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationActivationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private ClientRepository clientRepository;

  @Autowired private TokenRepository tokenRepository;

  @Autowired private ClientService clientService;

  @Autowired private AuthenticationService authenticationService;

  @BeforeEach
  public void setUp() {
    clientRepository.deleteAll();
    tokenRepository.deleteAll();
  }

  @Test
  public void testCompleteRegistrationToActivationRoundtrip() {
    // Register a new user
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("john_doe")
            .name("John")
            .surename("Doe")
            .email("john@example.com")
            .password("securepass123")
            .build();

    ResponseEntity<ClientResponse> registerResponse =
        restTemplate.postForEntity("/api/clients/register", registerRequest, ClientResponse.class);

    assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
    assertNotNull(registerResponse.getBody());
    assertEquals("john_doe", registerResponse.getBody().getNickname());
    assertEquals("john@example.com", registerResponse.getBody().getEmail());

    // Verify user is in PENDING_ACTIVATION status
    Optional<Client> savedClient = clientRepository.findByNickname("john_doe");
    assertTrue(savedClient.isPresent(), "Client should be saved");
    Client client = savedClient.get();
    assertEquals(UserStatus.PENDING_ACTIVATION, client.getStatus());

    // Verify activation token was created
    Optional<Token> activationTokenOpt = tokenRepository.findByClient(client);
    assertTrue(activationTokenOpt.isPresent(), "Activation token should be created");
    Token activationToken = activationTokenOpt.get();
    assertNotNull(activationToken.getToken());

    // Try to login before activation (should fail)
    LoginRequest loginRequest = new LoginRequest("john_doe", "securepass123");
    ResponseEntity<?> unauthorizedLogin =
        restTemplate.postForEntity("/api/clients/login", loginRequest, AuthResponse.class);
    assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedLogin.getStatusCode());

    // Activate the account with the token
    ResponseEntity<?> activateResponse =
        restTemplate.postForEntity(
            "/api/clients/activate?token=" + activationToken.getToken(), null, Object.class);

    assertEquals(HttpStatus.OK, activateResponse.getStatusCode());
    assertNotNull(activateResponse.getBody());

    // Verify user is now ACTIVE
    client = clientRepository.findByNickname("john_doe").orElseThrow();
    assertEquals(UserStatus.ACTIVE, client.getStatus());

    // Verify activation token is deleted
    Optional<Token> remainingToken = tokenRepository.findByClient(client);
    assertTrue(remainingToken.isEmpty(), "Activation token should be deleted after activation");

    // Login after activation (should succeed)
    ResponseEntity<AuthResponse> loginResponse =
        restTemplate.postForEntity("/api/clients/login", loginRequest, AuthResponse.class);

    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    assertNotNull(loginResponse.getBody());
    assertNotNull(loginResponse.getBody().getToken());
    assertEquals("john_doe", loginResponse.getBody().getNickname());
    assertEquals("john@example.com", loginResponse.getBody().getEmail());
  }

  @Test
  public void testRegistrationWithDuplicateNickname() {
    // Create existing user
    Client existingClient =
        Client.builder()
            .nickname("existing_user")
            .name("Existing")
            .surename("User")
            .email("existing@example.com")
            .password("hashed")
            .status(UserStatus.ACTIVE)
            .build();
    clientRepository.save(existingClient);

    // Try to register with same nickname
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("existing_user")
            .name("Another")
            .surename("Person")
            .email("another@example.com")
            .password("securepass123")
            .build();

    ResponseEntity<?> response =
        restTemplate.postForEntity("/api/clients/register", registerRequest, Object.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  public void testRegistrationWithDuplicateEmail() {
    // Create existing user
    Client existingClient =
        Client.builder()
            .nickname("existing_user")
            .name("Existing")
            .surename("User")
            .email("taken@example.com")
            .password("hashed")
            .status(UserStatus.ACTIVE)
            .build();
    clientRepository.save(existingClient);

    // Try to register with same email
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("new_user")
            .name("New")
            .surename("Person")
            .email("taken@example.com")
            .password("securepass123")
            .build();

    ResponseEntity<?> response =
        restTemplate.postForEntity("/api/clients/register", registerRequest, Object.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
  }

  @Test
  public void testActivationWithInvalidToken() {
    // Register a user
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("jane_doe")
            .name("Jane")
            .surename("Doe")
            .email("jane@example.com")
            .password("securepass123")
            .build();

    restTemplate.postForEntity("/api/clients/register", registerRequest, ClientResponse.class);

    // Try to activate with wrong token
    ResponseEntity<?> activateResponse =
        restTemplate.postForEntity(
            "/api/clients/activate?token=invalid-token-12345", null, Object.class);

    assertEquals(HttpStatus.BAD_REQUEST, activateResponse.getStatusCode());

    // Verify user is still in PENDING_ACTIVATION
    Client client = clientRepository.findByNickname("jane_doe").orElseThrow();
    assertEquals(UserStatus.PENDING_ACTIVATION, client.getStatus());
  }

  @Test
  public void testActivationTwice() {
    // Register a user
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("bob_smith")
            .name("Bob")
            .surename("Smith")
            .email("bob@example.com")
            .password("securepass123")
            .build();

    restTemplate.postForEntity("/api/clients/register", registerRequest, ClientResponse.class);

    // Get activation token
    Client client = clientRepository.findByNickname("bob_smith").orElseThrow();
    Token activationToken = tokenRepository.findByClient(client).orElseThrow();

    // Activate first time
    ResponseEntity<?> firstActivation =
        restTemplate.postForEntity(
            "/api/clients/activate?token=" + activationToken.getToken(), null, Object.class);
    assertEquals(HttpStatus.OK, firstActivation.getStatusCode());

    // Try to activate again with same token (should fail because token is deleted)
    ResponseEntity<?> secondActivation =
        restTemplate.postForEntity(
            "/api/clients/activate?token=" + activationToken.getToken(), null, Object.class);
    assertEquals(HttpStatus.BAD_REQUEST, secondActivation.getStatusCode());

    // But user is still ACTIVE
    client = clientRepository.findByNickname("bob_smith").orElseThrow();
    assertEquals(UserStatus.ACTIVE, client.getStatus());
  }

  @Test
  public void testLoginAfterRegistrationButBeforeActivation() {
    // Register a user
    ClientRegistrationRequest registerRequest =
        ClientRegistrationRequest.builder()
            .nickname("pending_user")
            .name("Pending")
            .surename("User")
            .email("pending@example.com")
            .password("securepass123")
            .build();

    restTemplate.postForEntity("/api/clients/register", registerRequest, ClientResponse.class);

    // Try to login before activation
    LoginRequest loginRequest = new LoginRequest("pending_user", "securepass123");
    ResponseEntity<?> loginResponse =
        restTemplate.postForEntity("/api/clients/login", loginRequest, Object.class);

    // Should fail because user is not ACTIVE
    assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());

    // Verify password is actually hashed
    Client client = clientRepository.findByNickname("pending_user").orElseThrow();
    assertFalse(
        client.getPassword().equals("securepass123"), "Password should be hashed, not plaintext");
  }
}
