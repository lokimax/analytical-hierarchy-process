package de.x132.ahp.controller;

import de.x132.ahp.dto.NodeRequest;
import de.x132.ahp.dto.NodeResponse;
import de.x132.ahp.mapper.NodeMapper;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.security.CheckOwnership;
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
  private final NodeMapper nodeMapper;

  public NodeController(
      NodeService nodeService,
      ProjectService projectService,
      AuthenticationService authenticationService,
      NodeMapper nodeMapper) {
    this.nodeService = nodeService;
    this.projectService = projectService;
    this.authenticationService = authenticationService;
    this.nodeMapper = nodeMapper;
  }

  @PostMapping
  public ResponseEntity<NodeResponse> createNode(
      Authentication authentication,
      @PathVariable String projectName,
      @Valid @RequestBody NodeRequest request) {
    String nickname = authentication.getName();
    Project project =
        projectService
            .findByClientNicknameAndName(nickname, projectName)
            .orElseThrow(() -> new RuntimeException("Project not found"));

    if (nodeService.findByProjectAndName(project, request.getName()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Node node = nodeMapper.toEntity(request);
    node.setProject(project);

    Node savedNode = nodeService.createNode(node);
    return ResponseEntity.status(HttpStatus.CREATED).body(nodeMapper.toResponse(savedNode));
  }

  @GetMapping
  public ResponseEntity<List<NodeResponse>> getAllNodes(
      Authentication authentication, @PathVariable String projectName) {
    String nickname = authentication.getName();
    Project project =
        projectService
            .findByClientNicknameAndName(nickname, projectName)
            .orElseThrow(() -> new RuntimeException("Project not found"));

    List<NodeResponse> nodes =
        nodeService.findAllByProject(project).stream()
            .map(nodeMapper::toResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(nodes);
  }

  @GetMapping("/{nodeName}")
  public ResponseEntity<NodeResponse> getNode(
      Authentication authentication,
      @PathVariable String projectName,
      @PathVariable String nodeName) {
    String nickname = authentication.getName();
    return nodeService
        .findByProjectClientNicknameAndProjectNameAndName(nickname, projectName, nodeName)
        .map(node -> ResponseEntity.ok(nodeMapper.toResponse(node)))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{nodeId}")
  @CheckOwnership(repository = de.x132.ahp.repository.NodeRepository.class, idParam = "nodeId")
  public ResponseEntity<Void> deleteNode(
      Authentication authentication, @PathVariable String projectName, @PathVariable Long nodeId) {

    nodeService.deleteNode(nodeId);
    return ResponseEntity.noContent().build();
  }
}
