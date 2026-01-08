package de.x132.ahp.controller;

import de.x132.ahp.dto.NodeRequest;
import de.x132.ahp.dto.NodeResponse;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.AuthenticationService;
import de.x132.ahp.service.NodeService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectName}/nodes")
public class NodeController {

  private final NodeService nodeService;
  private final ProjectService projectService;
  private final AuthenticationService authenticationService;

  public NodeController(
      NodeService nodeService,
      ProjectService projectService,
      AuthenticationService authenticationService) {
    this.nodeService = nodeService;
    this.projectService = projectService;
    this.authenticationService = authenticationService;
  }

  @PostMapping
  public ResponseEntity<NodeResponse> createNode(
      Authentication authentication,
      @PathVariable String projectName,
      @Valid @RequestBody NodeRequest request) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    String nickname = client.getNickname();
    Project project =
        projectService
            .findByClientNicknameAndName(nickname, projectName)
            .orElseThrow(() -> new RuntimeException("Project not found"));

    if (nodeService.findByProjectAndName(project, request.getName()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Node node =
        Node.builder()
            .name(request.getName())
            .beschreibung(request.getBeschreibung())
            .content(request.getContent())
            .project(project)
            .build();

    Node savedNode = nodeService.createNode(node);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedNode));
  }

  @GetMapping
  public ResponseEntity<List<NodeResponse>> getAllNodes(
      Authentication authentication, @PathVariable String projectName) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    String nickname = client.getNickname();
    Project project =
        projectService
            .findByClientNicknameAndName(nickname, projectName)
            .orElseThrow(() -> new RuntimeException("Project not found"));

    List<NodeResponse> nodes =
        nodeService.findAllByProject(project).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(nodes);
  }

  @GetMapping("/{nodeName}")
  public ResponseEntity<NodeResponse> getNode(
      Authentication authentication,
      @PathVariable String projectName,
      @PathVariable String nodeName) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    String nickname = client.getNickname();
    return nodeService
        .findByProjectClientNicknameAndProjectNameAndName(nickname, projectName, nodeName)
        .map(node -> ResponseEntity.ok(mapToResponse(node)))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{nodeName}")
  public ResponseEntity<Void> deleteNode(
      Authentication authentication,
      @PathVariable String projectName,
      @PathVariable String nodeName) {

    Client client = authenticationService.getAuthenticatedClient(authentication);
    String nickname = client.getNickname();
    Node node =
        nodeService
            .findByProjectClientNicknameAndProjectNameAndName(nickname, projectName, nodeName)
            .orElseThrow(() -> new RuntimeException("Node not found"));

    nodeService.deleteNode(node.getId());
    return ResponseEntity.noContent().build();
  }

  private NodeResponse mapToResponse(Node node) {
    return NodeResponse.builder()
        .id(node.getId())
        .name(node.getName())
        .beschreibung(node.getBeschreibung())
        .content(node.getContent())
        .build();
  }
}
