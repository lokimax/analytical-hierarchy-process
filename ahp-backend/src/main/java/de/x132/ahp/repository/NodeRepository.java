package de.x132.ahp.repository;

import de.x132.ahp.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Node entity.
 *
 * @author Max Wick
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {

    @Query("SELECT n FROM Node n WHERE n.project.client.nickname = :nickname AND n.project.name = :projectName")
    List<Node> findAllByProjectClientNicknameAndProjectName(@Param("nickname") String nickname, @Param("projectName") String projectName);

    @Query("SELECT n FROM Node n WHERE n.project.client.nickname = :nickname AND n.project.name = :projectName AND n.name = :nodeName")
    Optional<Node> findByProjectClientNicknameAndProjectNameAndName(@Param("nickname") String nickname, @Param("projectName") String projectName, @Param("nodeName") String nodeName);

    @Query("SELECT n FROM Node n WHERE n.project.client.nickname = :nickname AND n.project.name = :projectName AND n.ingoing IS EMPTY")
    List<Node> findStartNodes(@Param("nickname") String nickname, @Param("projectName") String projectName);

    @Query("SELECT n FROM Node n WHERE n.project.client.nickname = :nickname AND n.project.name = :projectName AND n.outgoing IS EMPTY")
    List<Node> findEndNodes(@Param("nickname") String nickname, @Param("projectName") String projectName);

    List<Node> findAllByProjectId(Long projectId);

    boolean existsByProjectIdAndName(Long projectId, String name);
}
