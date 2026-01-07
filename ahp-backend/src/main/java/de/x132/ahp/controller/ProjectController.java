package de.x132.ahp.controller;

import de.x132.ahp.dto.ProjectRequest;
import de.x132.ahp.dto.ProjectResponse;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.ClientService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients/{nickname}/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ClientService clientService;

    public ProjectController(ProjectService projectService, ClientService clientService) {
        this.projectService = projectService;
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable String nickname,
            @Valid @RequestBody ProjectRequest request) {
        
        Client client = clientService.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (projectService.existsByClientAndName(client, request.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Project project = Project.builder()
                .name(request.getName())
                .beschreibung(request.getBeschreibung())
                .client(client)
                .build();

        Project savedProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(savedProject));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@PathVariable String nickname) {
        Client client = clientService.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<ProjectResponse> projects = projectService.findAllByClient(client).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectName}")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable String nickname,
            @PathVariable String projectName) {
        
        return projectService.findByClientNicknameAndName(nickname, projectName)
                .map(project -> ResponseEntity.ok(mapToResponse(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{projectName}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String nickname,
            @PathVariable String projectName,
            @Valid @RequestBody ProjectRequest request) {
        
        Project project = projectService.findByClientNicknameAndName(nickname, projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setBeschreibung(request.getBeschreibung());

        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(mapToResponse(updatedProject));
    }

    @DeleteMapping("/{projectName}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String nickname,
            @PathVariable String projectName) {
        
        Project project = projectService.findByClientNicknameAndName(nickname, projectName)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectService.deleteProject(project.getId());
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
