package de.x132.ahp.repository;

import de.x132.ahp.model.Connection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Connection entity.
 *
 * @author Max Wick
 */
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

  List<Connection> findAllByProjectId(Long projectId);

  List<Connection> findAllByProject(de.x132.ahp.model.Project project);

  List<Connection> findAllBySourceNode(de.x132.ahp.model.Node sourceNode);

  List<Connection> findAllByTargetNode(de.x132.ahp.model.Node targetNode);

  List<Connection> findAllBySourceNodeId(Long sourceNodeId);

  List<Connection> findAllByTargetNodeId(Long targetNodeId);

  boolean existsBySourceNodeIdAndTargetNodeId(Long sourceNodeId, Long targetNodeId);
}
