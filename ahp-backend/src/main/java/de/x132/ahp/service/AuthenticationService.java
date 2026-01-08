package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Token;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for handling authentication operations.
 *
 * @author Max Wick
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Authenticates a user and generates a token.
     *
     * @param nickname the user's nickname
     * @param password the user's password
     * @return the generated authentication token
     * @throws RuntimeException if authentication fails
     */
    @Transactional
    public String login(String nickname, String password) {
        Optional<Client> clientOptional = clientRepository.findByNickname(nickname);

        if (clientOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        Client client = clientOptional.get();

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate token
        String tokenValue = generateToken();

        // Create or update token
        Token token = tokenRepository.findByClient(client)
                .orElse(Token.builder().client(client).build());

        token.setToken(tokenValue);
        token.setExpiresAt(LocalDateTime.now().plusDays(30)); // Token valid for 30 days
        tokenRepository.save(token);

        return tokenValue;
    }

    /**
     * Logs out a user by removing their token.
     *
     * @param tokenValue the authentication token
     */
    @Transactional
    public void logout(String tokenValue) {
        tokenRepository.findByToken(tokenValue).ifPresent(tokenRepository::delete);
    }

    /**
     * Validates a token and returns the associated client.
     *
     * @param tokenValue the authentication token
     * @return the client associated with the token
     */
    public Optional<Client> validateToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .map(Token::getClient);
    }

    /**
     * Generates a random authentication token.
     *
     * @return the generated token
     */
    private String generateToken() {
        byte[] tokenBytes = new byte[24];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
