package de.x132.ahp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for AHP Backend.
 *
 * @author Max Wick
 */
@SpringBootApplication
@EnableJpaRepositories
public class AhpApplication {

  public static void main(String[] args) {
    SpringApplication.run(AhpApplication.class, args);
  }
}
