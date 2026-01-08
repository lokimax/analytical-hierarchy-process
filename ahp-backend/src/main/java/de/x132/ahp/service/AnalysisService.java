package de.x132.ahp.service;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.AnalysisRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnalysisService {

  private final AnalysisRepository analysisRepository;

  public AnalysisService(AnalysisRepository analysisRepository) {
    this.analysisRepository = analysisRepository;
  }

  public Analysis createAnalysis(Analysis analysis) {
    return analysisRepository.save(analysis);
  }

  public List<Analysis> findAllByProject(Project project) {
    return analysisRepository.findByProjectOrderByCreatedAtDesc(project);
  }

  public Optional<Analysis> findByProjectAndName(Project project, String name) {
    return analysisRepository.findByProjectAndName(project, name);
  }

  public Optional<Analysis> findById(Long id) {
    return analysisRepository.findById(id);
  }

  public Analysis updateAnalysis(Analysis analysis) {
    return analysisRepository.save(analysis);
  }

  public void deleteAnalysis(Long id) {
    analysisRepository.deleteById(id);
  }
}
