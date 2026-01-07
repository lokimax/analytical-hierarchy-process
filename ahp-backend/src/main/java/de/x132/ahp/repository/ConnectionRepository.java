package de.x132.ahp.repository;

import de.x132.ahp.model.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Connection entity.
 *
 * @author Max Wick
 */
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findAllByProjectId(Long projectId);

    List<Connection> findAllBySourceNodeId(Long sourceNodeId);

    List<Connection> findAllByTargetNodeId(Long targetNodeId);

    boolean existsBySourceNodeIdAndTargetNodeId(Long sourceNodeId, Long targetNodeId);
}
