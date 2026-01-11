package de.x132.ahp.security;

import de.x132.ahp.exception.UnauthorizedException;
import de.x132.ahp.model.Client;
import de.x132.ahp.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  private final ClientRepository clientRepository;

  public SecurityUtils(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Client getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {
      throw new UnauthorizedException("No authenticated user found");
    }

    String username = authentication.getName();
    return clientRepository
        .findByNickname(username)
        .orElseThrow(() -> new UnauthorizedException("User not found: " + username));
  }

  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && !authentication.getPrincipal().equals("anonymousUser");
  }

  public String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {
      return "SYSTEM";
    }

    return authentication.getName();
  }
}
