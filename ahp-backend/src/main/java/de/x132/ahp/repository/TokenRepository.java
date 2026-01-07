package de.x132.ahp.repository;

import de.x132.ahp.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Token entity.
 *
 * @author Max Wick
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    List<Token> findAllByExpiresAtBefore(LocalDateTime dateTime);

    List<Token> findAllByClientId(Long clientId);

    void deleteByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime dateTime);

    void deleteAllByClientId(Long clientId);
}
