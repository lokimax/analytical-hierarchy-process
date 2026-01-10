package de.x132.ahp.util;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * Utility for generating secure tokens.
 *
 * @author Max Wick
 */
@Component
public class TokenGenerator {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int TOKEN_LENGTH = 32;

  /**
   * Generates a cryptographically secure token.
   *
   * @return a Base64-URL-encoded token
   */
  public String generateToken() {
    byte[] randomBytes = new byte[TOKEN_LENGTH];
    RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
