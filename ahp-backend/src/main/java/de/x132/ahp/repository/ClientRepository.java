package de.x132.ahp.repository;

import de.x132.ahp.model.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Client entity.
 *
 * @author Max Wick
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  Optional<Client> findByNickname(String nickname);

  Optional<Client> findByEmail(String email);

  Optional<Client> findByNicknameIgnoreCaseOrEmailIgnoreCase(String nickname, String email);

  Optional<Client> findByActivationCode(String activationCode);

  Optional<Client> findByNicknameAndPassword(String nickname, String password);

  @Query("SELECT c FROM Client c JOIN FETCH c.tokens t WHERE t.token = :token")
  Optional<Client> findByToken(@Param("token") String token);

  boolean existsByNickname(String nickname);

  boolean existsByEmail(String email);
}
