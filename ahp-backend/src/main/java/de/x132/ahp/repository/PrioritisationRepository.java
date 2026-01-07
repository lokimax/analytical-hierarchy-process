package de.x132.ahp.repository;

import de.x132.ahp.model.Prioritisation;
import de.x132.ahp.model.SolvingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Prioritisation entity.
 *
 * @author Max Wick
 */
@Repository
public interface PrioritisationRepository extends JpaRepository<Prioritisation, Long> {

    @Query("SELECT p FROM Prioritisation p WHERE p.project.client.nickname = :nickname AND p.project.name = :projectName")
    List<Prioritisation> findAllByProjectClientNicknameAndProjectName(@Param("nickname") String nickname, @Param("projectName") String projectName);

    @Query("SELECT p FROM Prioritisation p WHERE p.project.client.nickname = :nickname AND p.project.name = :projectName AND p.name = :prioritisationName")
    Optional<Prioritisation> findByProjectClientNicknameAndProjectNameAndName(@Param("nickname") String nickname, @Param("projectName") String projectName, @Param("prioritisationName") String prioritisationName);

    @Query("SELECT p.method FROM Prioritisation p WHERE p.project.client.nickname = :nickname AND p.project.name = :projectName AND p.name = :prioritisationName")
    Optional<SolvingMethod> findMethodByProjectClientNicknameAndProjectNameAndName(@Param("nickname") String nickname, @Param("projectName") String projectName, @Param("prioritisationName") String prioritisationName);
    List<Prioritisation> findAllByProject(de.x132.ahp.model.Project project);

    Optional<Prioritisation> findByProjectAndName(de.x132.ahp.model.Project project, String name);
    List<Prioritisation> findAllByProjectId(Long projectId);

    boolean existsByProjectIdAndName(Long projectId, String name);
}
