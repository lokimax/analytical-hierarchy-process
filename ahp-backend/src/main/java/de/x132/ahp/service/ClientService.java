package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Token;
import de.x132.ahp.model.TokenType;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.TokenRepository;
import de.x132.ahp.util.TokenGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling client operations including registration, activation, and password reset.
 *
 * @author Max Wick
 */
@Service
@Transactional
public class ClientService {

  private final ClientRepository clientRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenGenerator tokenGenerator;

  @Value("${app.activation.token-expiry-hours:24}")
  private Integer tokenExpiryHours;

  public ClientService(
      ClientRepository clientRepository,
      TokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      TokenGenerator tokenGenerator) {
    this.clientRepository = clientRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenGenerator = tokenGenerator;
  }

  public Client registerClient(Client client) {
    client.setPassword(passwordEncoder.encode(client.getPassword()));
    client.setStatus(UserStatus.PENDING_ACTIVATION);
    client.setActivationCode(generateActivationCode());
    return clientRepository.save(client);
  }

  public Optional<Client> findById(Long id) {
    return clientRepository.findById(id);
  }

  public Optional<Client> findByNickname(String nickname) {
    return clientRepository.findByNickname(nickname);
  }

  public Optional<Client> findByEmail(String email) {
    return clientRepository.findByEmail(email);
  }

  public Optional<Client> getClientByIdentifier(String identifier) {
    return clientRepository.findByNicknameIgnoreCaseOrEmailIgnoreCase(identifier, identifier);
  }

  public boolean activateClient(String activationCode) {
    Optional<Client> clientOpt = clientRepository.findByActivationCode(activationCode);
    if (clientOpt.isEmpty()) {
      return false;
    }

    Client client = clientOpt.get();
    client.setStatus(UserStatus.ACTIVE);
    client.setActivationCode(null);
    clientRepository.save(client);
    return true;
  }

  public Client updateClient(Client client) {
    return clientRepository.save(client);
  }

  public void deleteClient(Long id) {
    clientRepository.deleteById(id);
  }

  public List<Client> findAll() {
    return clientRepository.findAll();
  }

  public boolean authenticate(String nickname, String password) {
    Optional<Client> clientOpt = clientRepository.findByNickname(nickname);
    if (clientOpt.isEmpty()) {
      return false;
    }

    Client client = clientOpt.get();
    return client.getStatus() == UserStatus.ACTIVE
        && passwordEncoder.matches(password, client.getPassword());
  }

  public Token createToken(Client client, LocalDateTime expiresAt) {
    Token token =
        Token.builder()
            .token(UUID.randomUUID().toString())
            .client(client)
            .expiresAt(expiresAt)
            .type(TokenType.AUTHENTICATION)
            .build();
    return tokenRepository.save(token);
  }

  public Optional<Token> findTokenByValue(String tokenValue) {
    return tokenRepository.findByToken(tokenValue);
  }

  public Optional<Client> findClientByToken(String tokenValue) {
    return tokenRepository
        .findByToken(tokenValue)
        .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
        .map(Token::getClient);
  }

  public void deleteToken(String tokenValue) {
    tokenRepository.findByToken(tokenValue).ifPresent(token -> tokenRepository.delete(token));
  }

  public void deleteExpiredTokens() {
    List<Token> expiredTokens = tokenRepository.findAllByExpiresAtBefore(LocalDateTime.now());
    tokenRepository.deleteAll(expiredTokens);
  }

  private String generateActivationCode() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
  }

  public boolean existsByNickname(String nickname) {
    return clientRepository.findByNickname(nickname).isPresent();
  }

  public boolean existsByEmail(String email) {
    return clientRepository.findByEmail(email).isPresent();
  }

  /**
   * Creates an activation token for a client.
   *
   * @param client the client to create the token for
   * @return the generated token
   */
  public Token createActivationToken(Client client) {
    String tokenValue = tokenGenerator.generateToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpiryHours);
    Token token =
        Token.builder()
            .token(tokenValue)
            .client(client)
            .expiresAt(expiresAt)
            .type(TokenType.ACTIVATION)
            .build();
    return tokenRepository.save(token);
  }

  /**
   * Activates a client using an activation token.
   *
   * @param tokenValue the activation token
   * @return "success" if activated, "already_active" if client is already active, null if token is
   *     invalid or expired
   */
  public String activateClientWithToken(String tokenValue) {
    Optional<Token> tokenOpt = tokenRepository.findByToken(tokenValue);
    if (tokenOpt.isEmpty()) {
      return null;
    }

    Token token = tokenOpt.get();
    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      return null;
    }

    Client client = token.getClient();

    // Check if already activated
    if (client.getStatus() == UserStatus.ACTIVE) {
      return "already_active";
    }

    client.setStatus(UserStatus.ACTIVE);
    clientRepository.save(client);
    tokenRepository.delete(token);

    return "success";
  }

  /**
   * Creates a password reset token for a client.
   *
   * @param email the email of the client
   * @return the generated token, or empty if client not found
   */
  public Optional<Token> createPasswordResetToken(String email) {
    Optional<Client> clientOpt = clientRepository.findByEmail(email);
    if (clientOpt.isEmpty()) {
      return Optional.empty();
    }

    String tokenValue = tokenGenerator.generateToken();
    LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpiryHours);
    Token token =
        Token.builder()
            .token(tokenValue)
            .client(clientOpt.get())
            .expiresAt(expiresAt)
            .type(TokenType.PASSWORD_RESET)
            .build();
    return Optional.of(tokenRepository.save(token));
  }

  /**
   * Resets the client's password using a reset token.
   *
   * @param tokenValue the reset token
   * @param newPassword the new password
   * @return true if successful, false if token invalid/expired
   */
  public boolean resetPassword(String tokenValue, String newPassword) {
    Optional<Token> tokenOpt = tokenRepository.findByToken(tokenValue);
    if (tokenOpt.isEmpty()) {
      return false;
    }

    Token token = tokenOpt.get();
    if (token.getExpiresAt().isBefore(LocalDateTime.now())
        || token.getType() != TokenType.PASSWORD_RESET) {
      return false;
    }

    Client client = token.getClient();
    client.setPassword(passwordEncoder.encode(newPassword));
    clientRepository.save(client);
    tokenRepository.delete(token);

    return true;
  }
}
