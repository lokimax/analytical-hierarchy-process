package de.x132.ahp.controller;

import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.dto.AnalysisResponse;
import de.x132.ahp.exception.ResourceNotFoundException;
import de.x132.ahp.exception.ValidationException;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectName}/analyses")
public class AnalysisController {

  private final AnalysisService analysisService;
  private final ProjectService projectService;
  private final AuthenticationService authenticationService;

  public AnalysisController(
      AnalysisService analysisService,
      ProjectService projectService,
      AuthenticationService authenticationService) {
    this.analysisService = analysisService;
    this.projectService = projectService;
    this.authenticationService = authenticationService;
  }

  @PostMapping
  public ResponseEntity<AnalysisResponse> createAnalysis(
      @PathVariable String projectName,
      @Valid @RequestBody AnalysisRequest request,
      Authentication authentication) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    Project project =
        projectService
            .findByClientNicknameAndName(client.getNickname(), projectName)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "name", projectName));

    if (analysisService.findByProjectAndName(project, request.getName()).isPresent()) {
      throw new ValidationException(
          "Analysis with name '" + request.getName() + "' already exists in this project");
    }

    Analysis analysis =
        Analysis.builder()
            .name(request.getName())
            .beschreibung(request.getBeschreibung())
            .project(project)
            .criteriaComparisons(request.getCriteriaComparisons())
            .alternativeComparisons(request.getAlternativeComparisons())
            .results(request.getResults())
            .completedAt(LocalDateTime.now())
            .build();

    Analysis savedAnalysis = analysisService.createAnalysis(analysis);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedAnalysis));
  }

  @GetMapping
  public ResponseEntity<List<AnalysisResponse>> getAllAnalyses(
      @PathVariable String projectName, Authentication authentication) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    Project project =
        projectService
            .findByClientNicknameAndName(client.getNickname(), projectName)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "name", projectName));

    List<AnalysisResponse> analyses =
        analysisService.findAllByProject(project).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(analyses);
  }

  @GetMapping("/{analysisId}")
  public ResponseEntity<AnalysisResponse> getAnalysis(
      @PathVariable String projectName,
      @PathVariable Long analysisId,
      Authentication authentication) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    projectService
        .findByClientNicknameAndName(client.getNickname(), projectName)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "name", projectName));

    Analysis analysis =
        analysisService
            .findById(analysisId)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", analysisId));
    return ResponseEntity.ok(mapToResponse(analysis));
  }

  @DeleteMapping("/{analysisId}")
  public ResponseEntity<Void> deleteAnalysis(
      @PathVariable String projectName,
      @PathVariable Long analysisId,
      Authentication authentication) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    projectService
        .findByClientNicknameAndName(client.getNickname(), projectName)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "name", projectName));

    // Verify analysis exists before deleting
    analysisService
        .findById(analysisId)
        .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", analysisId));

    analysisService.deleteAnalysis(analysisId);
    return ResponseEntity.noContent().build();
  }

  private AnalysisResponse mapToResponse(Analysis analysis) {
    return AnalysisResponse.builder()
        .id(analysis.getId())
        .name(analysis.getName())
        .beschreibung(analysis.getBeschreibung())
        .criteriaComparisons(analysis.getCriteriaComparisons())
        .alternativeComparisons(analysis.getAlternativeComparisons())
        .results(analysis.getResults())
        .completedAt(analysis.getCompletedAt())
        .createdAt(analysis.getCreatedAt())
        .updatedAt(analysis.getUpdatedAt())
        .build();
  }
}
