package de.x132.ahp.repository;

import de.x132.ahp.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Project entity.
 *
 * @author Max Wick
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

  @Query("SELECT p FROM Project p WHERE p.client.nickname = :nickname")
  List<Project> findAllByClientNickname(@Param("nickname") String nickname);

  @Query("SELECT p FROM Project p WHERE p.client.nickname = :nickname AND p.name = :projectName")
  Optional<Project> findByClientNicknameAndName(
      @Param("nickname") String nickname, @Param("projectName") String projectName);

  List<Project> findAllByClientId(Long clientId);

  List<Project> findAllByClient(de.x132.ahp.model.Client client);

  Optional<Project> findByClientAndName(de.x132.ahp.model.Client client, String name);

  boolean existsByClientIdAndName(Long clientId, String name);
}
