package de.x132.ahp.repository;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

  List<Analysis> findByProjectOrderByCreatedAtDesc(Project project);

  Optional<Analysis> findByProjectAndName(Project project, String name);
}
