package de.x132.ahp.controller;

import de.x132.ahp.dto.ProjectRequest;
import de.x132.ahp.dto.ProjectResponse;
import de.x132.ahp.exception.ResourceNotFoundException;
import de.x132.ahp.exception.UnauthorizedException;
import de.x132.ahp.exception.ValidationException;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.ClientService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService projectService;
  private final ClientService clientService;

  public ProjectController(ProjectService projectService, ClientService clientService) {
    this.projectService = projectService;
    this.clientService = clientService;
  }

  @PostMapping
  public ResponseEntity<ProjectResponse> createProject(
      Authentication authentication, @Valid @RequestBody ProjectRequest request) {

    String nickname = authentication.getName();
    Client client =
        clientService
            .findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("Client", "nickname", nickname));

    if (projectService.existsByClientAndName(client, request.getName())) {
      throw new ValidationException("Project with name '" + request.getName() + "' already exists");
    }

    Project project =
        Project.builder()
            .name(request.getName())
            .beschreibung(request.getBeschreibung())
            .client(client)
            .build();

    Project savedProject = projectService.createProject(project);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedProject));
  }

  @GetMapping
  public ResponseEntity<List<ProjectResponse>> getAllProjects(Authentication authentication) {
    String nickname = authentication.getName();
    Client client =
        clientService
            .findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("Client", "nickname", nickname));

    List<ProjectResponse> projects =
        projectService.findAllByClient(client).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

    return projects.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(projects);
  }

  @GetMapping("/{projectName}")
  public ResponseEntity<ProjectResponse> getProject(
      Authentication authentication, @PathVariable String projectName) {

    String nickname = authentication.getName();
    Project project =
        projectService
            .findByClientNicknameAndName(nickname, projectName)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "name", projectName));
    return ResponseEntity.ok(mapToResponse(project));
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<ProjectResponse> updateProject(
      Authentication authentication,
      @PathVariable Long projectId,
      @Valid @RequestBody ProjectRequest request) {

    Project project =
        projectService
            .findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    // Verify ownership
    if (!project.getClient().getNickname().equals(authentication.getName())) {
      throw new UnauthorizedException("You do not have permission to update this project");
    }

    project.setBeschreibung(request.getBeschreibung());

    Project updatedProject = projectService.updateProject(project);
    return ResponseEntity.ok(mapToResponse(updatedProject));
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<Void> deleteProject(
      Authentication authentication, @PathVariable Long projectId) {

    Project project =
        projectService
            .findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    // Verify ownership
    if (!project.getClient().getNickname().equals(authentication.getName())) {
      throw new UnauthorizedException("You do not have permission to delete this project");
    }

    projectService.deleteProject(projectId);
    return ResponseEntity.noContent().build();
  }

  private ProjectResponse mapToResponse(Project project) {
    return ProjectResponse.builder()
        .id(project.getId())
        .name(project.getName())
        .beschreibung(project.getBeschreibung())
        .clientNickname(project.getClient().getNickname())
        .createdAt(project.getCreatedAt())
        .updatedAt(project.getUpdatedAt())
        .build();
  }
}
