package de.x132.ahp.config;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializationConfig {

  @Bean
  public ApplicationRunner initializeDefaultClient(
      ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      if (!clientRepository.findByNickname("default").isPresent()) {
        Client defaultClient =
            Client.builder()
                .nickname("default")
                .name("Default")
                .surename("User")
                .email("default@ahp.local")
                .password(passwordEncoder.encode("default"))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .activationCode(null)
                .build();

        clientRepository.save(defaultClient);
        System.out.println("Default client created successfully");
      }

      if (!clientRepository.findByNickname("xntradm").isPresent()) {
        Client xntradmClient =
            Client.builder()
                .nickname("xntradm")
                .name("Xntr")
                .surename("Admin")
                .email("xntradm@ahp.local")
                .password(passwordEncoder.encode("xntradm"))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .activationCode(null)
                .build();

        clientRepository.save(xntradmClient);
        System.out.println("xntradm client created successfully");
      }
    };
  }
}
