package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Token;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.TokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository,
                         TokenRepository tokenRepository,
                         PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
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
        return client.getStatus() == UserStatus.ACTIVE && 
               passwordEncoder.matches(password, client.getPassword());
    }

    public Token createToken(Client client, LocalDateTime expiresAt) {
        Token token = Token.builder()
                .token(UUID.randomUUID().toString())
                .client(client)
                .expiresAt(expiresAt)
                .build();
        return tokenRepository.save(token);
    }

    public Optional<Token> findTokenByValue(String tokenValue) {
        return tokenRepository.findByToken(tokenValue);
    }

    public Optional<Client> findClientByToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(Token::getClient);
    }

    public void deleteToken(String tokenValue) {
        tokenRepository.findByToken(tokenValue)
                .ifPresent(token -> tokenRepository.delete(token));
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
}
