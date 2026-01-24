package de.x132.ahp.controller;

import de.x132.ahp.dto.ConnectionRequest;
import de.x132.ahp.dto.ConnectionResponse;
import de.x132.ahp.mapper.ConnectionMapper;
import de.x132.ahp.model.Connection;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.service.NodeService;
import de.x132.ahp.service.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectName}/connections")
public class ConnectionController {

  private final NodeService nodeService;
  private final ProjectService projectService;
  private final ConnectionMapper connectionMapper;

  public ConnectionController(
      NodeService nodeService, ProjectService projectService, ConnectionMapper connectionMapper) {
    this.nodeService = nodeService;
    this.projectService = projectService;
    this.connectionMapper = connectionMapper;
  }

  @PostMapping
  public ResponseEntity<ConnectionResponse> createConnection(
      @PathVariable String projectName, @Valid @RequestBody ConnectionRequest request) {

    // Find the project (assuming we can identify it by name alone for this
    // endpoint)
    Project project =
        projectService.findAll().stream()
            .filter(p -> p.getName().equals(projectName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Project not found"));

    // Find source and target nodes
    Node sourceNode =
        nodeService
            .findByProjectAndName(project, request.getSourceNodeName())
            .orElseThrow(() -> new RuntimeException("Source node not found"));

    Node targetNode =
        nodeService
            .findByProjectAndName(project, request.getTargetNodeName())
            .orElseThrow(() -> new RuntimeException("Target node not found"));

    Connection connection = connectionMapper.toEntity(request);
    connection.setSourceNode(sourceNode);
    connection.setTargetNode(targetNode);
    connection.setProject(project);

    Connection savedConnection = nodeService.createConnection(connection);
    return ResponseEntity.ok(connectionMapper.toResponse(savedConnection));
  }

  @GetMapping
  public ResponseEntity<List<ConnectionResponse>> getAllConnections(
      @PathVariable String projectName) {
    Project project =
        projectService.findAll().stream()
            .filter(p -> p.getName().equals(projectName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Project not found"));

    List<ConnectionResponse> connections =
        nodeService.findConnectionsByProject(project).stream()
            .map(connectionMapper::toResponse)
            .collect(Collectors.toList());

    return ResponseEntity.ok(connections);
  }

  @DeleteMapping("/{connectionId}")
  public ResponseEntity<Void> deleteConnection(
      @PathVariable String projectName, @PathVariable Long connectionId) {

    nodeService.deleteConnection(connectionId);
    return ResponseEntity.noContent().build();
  }
}
