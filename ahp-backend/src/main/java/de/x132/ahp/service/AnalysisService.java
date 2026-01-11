package de.x132.ahp.service;

import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
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

  /**
   * Check if a user owns an analysis (by checking project ownership).
   *
   * @param analysisId the analysis ID
   * @param user the user to check
   * @return true if the user owns the analysis (via project ownership), false otherwise
   */
  public boolean isOwner(Long analysisId, Client user) {
    return analysisRepository
        .findById(analysisId)
        .map(analysis -> analysis.getProject().getClient().equals(user))
        .orElse(false);
  }

  /**
   * Get an analysis if the user owns it (via project ownership).
   *
   * @param analysisId the analysis ID
   * @param user the user who should own the analysis
   * @return an Optional containing the analysis if the user owns it, empty otherwise
   */
  public Optional<Analysis> getByIdAndOwner(Long analysisId, Client user) {
    return analysisRepository
        .findById(analysisId)
        .filter(analysis -> analysis.getProject().getClient().equals(user));
  }

  /**
   * Get all analyses in a specific project.
   *
   * @param project the project to get analyses from
   * @return a list of all analyses in the project
   */
  public List<Analysis> getAllByProject(Project project) {
    return analysisRepository.findByProjectOrderByCreatedAtDesc(project);
  }
}
