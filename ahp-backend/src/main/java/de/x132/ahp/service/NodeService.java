package de.x132.ahp.service;

import de.x132.ahp.model.Client;
import de.x132.ahp.model.Connection;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ConnectionRepository;
import de.x132.ahp.repository.NodeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NodeService {

  private final NodeRepository nodeRepository;
  private final ConnectionRepository connectionRepository;

  public NodeService(NodeRepository nodeRepository, ConnectionRepository connectionRepository) {
    this.nodeRepository = nodeRepository;
    this.connectionRepository = connectionRepository;
  }

  public Node createNode(Node node) {
    return nodeRepository.save(node);
  }

  public Optional<Node> findById(Long id) {
    return nodeRepository.findById(id);
  }

  public List<Node> findAllByProject(Project project) {
    return nodeRepository.findAllByProject(project);
  }

  public Optional<Node> findByProjectAndName(Project project, String name) {
    return nodeRepository.findByProjectAndName(project, name);
  }

  public Optional<Node> findByProjectClientNicknameAndProjectNameAndName(
      String clientNickname, String projectName, String nodeName) {
    return nodeRepository.findByProjectClientNicknameAndProjectNameAndName(
        clientNickname, projectName, nodeName);
  }

  public Node updateNode(Node node) {
    return nodeRepository.save(node);
  }

  public void deleteNode(Long id) {
    nodeRepository.deleteById(id);
  }

  public List<Node> findAll() {
    return nodeRepository.findAll();
  }

  public Connection createConnection(Connection connection) {
    return connectionRepository.save(connection);
  }

  public List<Connection> findConnectionsByProject(Project project) {
    return connectionRepository.findAllByProject(project);
  }

  public List<Connection> findConnectionsBySourceNode(Node sourceNode) {
    return connectionRepository.findAllBySourceNode(sourceNode);
  }

  public List<Connection> findConnectionsByTargetNode(Node targetNode) {
    return connectionRepository.findAllByTargetNode(targetNode);
  }

  public void deleteConnection(Long id) {
    connectionRepository.deleteById(id);
  }

  public boolean existsByProjectAndName(Project project, String name) {
    return nodeRepository.findByProjectAndName(project, name).isPresent();
  }

  /**
   * Check if a user owns a node (by checking project ownership).
   *
   * @param nodeId the node ID
   * @param user the user to check
   * @return true if the user owns the node (via project ownership), false otherwise
   */
  public boolean isOwner(Long nodeId, Client user) {
    return nodeRepository
        .findById(nodeId)
        .map(node -> node.getProject().getClient().equals(user))
        .orElse(false);
  }

  /**
   * Get a node if the user owns it (via project ownership).
   *
   * @param nodeId the node ID
   * @param user the user who should own the node
   * @return an Optional containing the node if the user owns it, empty otherwise
   */
  public Optional<Node> getByIdAndOwner(Long nodeId, Client user) {
    return nodeRepository
        .findById(nodeId)
        .filter(node -> node.getProject().getClient().equals(user));
  }

  /**
   * Get all nodes in a specific project.
   *
   * @param project the project to get nodes from
   * @return a list of all nodes in the project
   */
  public List<Node> getAllByProject(Project project) {
    return nodeRepository.findAllByProject(project);
  }
}
