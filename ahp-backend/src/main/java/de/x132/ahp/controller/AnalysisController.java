package de.x132.ahp.controller;

import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.dto.AnalysisResponse;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.AnalysisService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients/{nickname}/projects/{projectName}/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final ProjectService projectService;

    public AnalysisController(AnalysisService analysisService, ProjectService projectService) {
        this.analysisService = analysisService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse> createAnalysis(
            @PathVariable String nickname,
            @PathVariable String projectName,
            @Valid @RequestBody AnalysisRequest request) {
        
        Project project = projectService.findByClientNicknameAndName(nickname, projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (analysisService.findByProjectAndName(project, request.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Analysis analysis = Analysis.builder()
                .name(request.getName())
                .beschreibung(request.getBeschreibung())
                .project(project)
                .criteriaComparisons(request.getCriteriaComparisons())
                .alternativeComparisons(request.getAlternativeComparisons())
                .results(request.getResults())
                .completedAt(LocalDateTime.now())
                .build();

        Analysis savedAnalysis = analysisService.createAnalysis(analysis);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(savedAnalysis));
    }

    @GetMapping
    public ResponseEntity<List<AnalysisResponse>> getAllAnalyses(
            @PathVariable String nickname,
            @PathVariable String projectName) {
        
        Project project = projectService.findByClientNicknameAndName(nickname, projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<AnalysisResponse> analyses = analysisService.findAllByProject(project).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponse> getAnalysis(
            @PathVariable String nickname,
            @PathVariable String projectName,
            @PathVariable Long analysisId) {
        
        return analysisService.findById(analysisId)
                .map(analysis -> ResponseEntity.ok(mapToResponse(analysis)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable String nickname,
            @PathVariable String projectName,
            @PathVariable Long analysisId) {
        
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
