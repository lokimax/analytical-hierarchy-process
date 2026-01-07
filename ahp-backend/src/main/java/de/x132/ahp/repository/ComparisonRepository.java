package de.x132.ahp.repository;

import de.x132.ahp.model.Comparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Comparison entity.
 *
 * @author Max Wick
 */
@Repository
public interface ComparisonRepository extends JpaRepository<Comparison, Long> {

    @Query("SELECT c FROM Comparison c WHERE c.prioritisation.project.client.nickname = :nickname " +
            "AND c.prioritisation.project.name = :projectName " +
            "AND c.prioritisation.name = :prioritisationName " +
            "AND c.parent.name = :parentNodeName " +
            "AND ((c.leftNode.name = :leftNodeName AND c.rightNode.name = :rightNodeName) " +
            "OR (c.leftNode.name = :rightNodeName AND c.rightNode.name = :leftNodeName))")
    Optional<Comparison> findComparison(@Param("nickname") String nickname,
                                        @Param("projectName") String projectName,
                                        @Param("prioritisationName") String prioritisationName,
                                        @Param("parentNodeName") String parentNodeName,
                                        @Param("leftNodeName") String leftNodeName,
                                        @Param("rightNodeName") String rightNodeName);

    List<Comparison> findAllByPrioritisationId(Long prioritisationId);

    List<Comparison> findAllByParentId(Long parentNodeId);

    void deleteAllByPrioritisationId(Long prioritisationId);
}
