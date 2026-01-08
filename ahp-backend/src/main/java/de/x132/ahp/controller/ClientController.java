package de.x132.ahp.controller;

import de.x132.ahp.dto.AuthResponse;
import de.x132.ahp.dto.ClientRegistrationRequest;
import de.x132.ahp.dto.ClientResponse;
import de.x132.ahp.dto.LoginRequest;
import de.x132.ahp.model.Client;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

  private final ClientService clientService;
  private final AuthenticationService authenticationService;

  public ClientController(
      ClientService clientService, AuthenticationService authenticationService) {
    this.clientService = clientService;
    this.authenticationService = authenticationService;
  }

  @PostMapping("/register")
  public ResponseEntity<ClientResponse> register(
      @Valid @RequestBody ClientRegistrationRequest request) {
    if (clientService.existsByNickname(request.getNickname())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    if (clientService.existsByEmail(request.getEmail())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Client client =
        Client.builder()
            .nickname(request.getNickname())
            .name(request.getName())
            .surename(request.getSurename())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();

    Client savedClient = clientService.registerClient(client);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedClient));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    try {
      String tokenValue = authenticationService.login(request.getNickname(), request.getPassword());

      Client client =
          clientService
              .findByNickname(request.getNickname())
              .orElseThrow(() -> new RuntimeException("Client not found"));

      AuthResponse response =
          AuthResponse.builder()
              .token(tokenValue)
              .nickname(client.getNickname())
              .name(client.getName())
              .surename(client.getSurename())
              .email(client.getEmail())
              .build();

      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @DeleteMapping("/logout")
  public ResponseEntity<Void> logout(@RequestHeader("X-Auth-Token") String token) {
    authenticationService.logout(token);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/activate")
  public ResponseEntity<Void> activate(@RequestParam String code) {
    boolean activated = clientService.activateClient(code);
    if (activated) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @GetMapping("/{nickname}")
  public ResponseEntity<ClientResponse> getClient(
      @PathVariable String nickname,
      @RequestHeader(value = "X-Auth-Token", required = false) String token) {
    // Validate token
    if (token == null || authenticationService.validateToken(token).isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return clientService
        .findByNickname(nickname)
        .map(client -> ResponseEntity.ok(mapToResponse(client)))
        .orElse(ResponseEntity.notFound().build());
  }

  private ClientResponse mapToResponse(Client client) {
    return ClientResponse.builder()
        .id(client.getId())
        .nickname(client.getNickname())
        .name(client.getName())
        .surename(client.getSurename())
        .email(client.getEmail())
        .status(client.getStatus())
        .build();
  }
}
