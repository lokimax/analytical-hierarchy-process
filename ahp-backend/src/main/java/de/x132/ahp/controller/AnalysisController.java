package de.x132.ahp.controller;

import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.dto.AnalysisResponse;
import de.x132.ahp.dto.SensitivityResult;
import de.x132.ahp.exception.ResourceNotFoundException;
import de.x132.ahp.exception.ValidationException;
import de.x132.ahp.mapper.AnalysisMapper;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.AnalysisRepository;
import de.x132.ahp.security.CheckOwnership;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.ProjectService;
import de.x132.ahp.service.SensitivityAnalysisService;
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
  private final SensitivityAnalysisService sensitivityAnalysisService;
  private final AnalysisMapper analysisMapper;

  public AnalysisController(
      AnalysisService analysisService,
      ProjectService projectService,
      AuthenticationService authenticationService,
      SensitivityAnalysisService sensitivityAnalysisService,
      AnalysisMapper analysisMapper) {
    this.analysisService = analysisService;
    this.projectService = projectService;
    this.authenticationService = authenticationService;
    this.sensitivityAnalysisService = sensitivityAnalysisService;
    this.analysisMapper = analysisMapper;
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

    Analysis analysis = analysisMapper.toEntity(request);
    analysis.setProject(project);
    analysis.setCompletedAt(LocalDateTime.now());

    Analysis savedAnalysis = analysisService.createAnalysis(analysis);
    return ResponseEntity.status(HttpStatus.CREATED).body(analysisMapper.toResponse(savedAnalysis));
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
            .map(analysisMapper::toResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(analyses);
  }

  @GetMapping("/{analysisId}")
  @CheckOwnership(repository = AnalysisRepository.class, idParam = "analysisId")
  public ResponseEntity<AnalysisResponse> getAnalysis(
      @PathVariable String projectName,
      @PathVariable Long analysisId,
      Authentication authentication) {

    Analysis analysis =
        analysisService
            .findById(analysisId)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", analysisId));
    return ResponseEntity.ok(analysisMapper.toResponse(analysis));
  }

  @DeleteMapping("/{analysisId}")
  @CheckOwnership(repository = AnalysisRepository.class, idParam = "analysisId")
  public ResponseEntity<Void> deleteAnalysis(
      @PathVariable String projectName,
      @PathVariable Long analysisId,
      Authentication authentication) {

    analysisService.deleteAnalysis(analysisId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{analysisId}/sensitivity/{criterionId}")
  @CheckOwnership(repository = AnalysisRepository.class, idParam = "analysisId")
  public ResponseEntity<SensitivityResult> getSensitivityAnalysis(
      @PathVariable String projectName,
      @PathVariable Long analysisId,
      @PathVariable Long criterionId,
      Authentication authentication) {

    Analysis analysis =
        analysisService
            .findById(analysisId)
            .orElseThrow(() -> new ResourceNotFoundException("Analysis", "id", analysisId));

    SensitivityResult result = sensitivityAnalysisService.analyzeSensitivity(analysis, criterionId);
    return ResponseEntity.ok(result);
  }
}
