package de.x132.ahp.service;

import de.x132.ahp.core.dto.FullResultDTO;
import de.x132.ahp.core.solver.Solver;
import de.x132.ahp.model.Comparison;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Prioritisation;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ComparisonRepository;
import de.x132.ahp.repository.PrioritisationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PrioritisationService {

  private final PrioritisationRepository prioritisationRepository;
  private final ComparisonRepository comparisonRepository;
  private final Solver solver;

  public PrioritisationService(
      PrioritisationRepository prioritisationRepository,
      ComparisonRepository comparisonRepository,
      Solver solver) {
    this.prioritisationRepository = prioritisationRepository;
    this.comparisonRepository = comparisonRepository;
    this.solver = solver;
  }

  public Prioritisation createPrioritisation(Prioritisation prioritisation) {
    return prioritisationRepository.save(prioritisation);
  }

  public Optional<Prioritisation> findById(Long id) {
    return prioritisationRepository.findById(id);
  }

  public List<Prioritisation> findAllByProject(Project project) {
    return prioritisationRepository.findAllByProject(project);
  }

  public Optional<Prioritisation> findByProjectAndName(Project project, String name) {
    return prioritisationRepository.findByProjectAndName(project, name);
  }

  public Optional<Prioritisation> findByProjectClientNicknameAndProjectNameAndName(
      String clientNickname, String projectName, String prioritisationName) {
    return prioritisationRepository.findByProjectClientNicknameAndProjectNameAndName(
        clientNickname, projectName, prioritisationName);
  }

  public Prioritisation updatePrioritisation(Prioritisation prioritisation) {
    return prioritisationRepository.save(prioritisation);
  }

  public void deletePrioritisation(Long id) {
    prioritisationRepository.deleteById(id);
  }

  public List<Prioritisation> findAll() {
    return prioritisationRepository.findAll();
  }

  public Comparison addComparison(Comparison comparison) {
    return comparisonRepository.save(comparison);
  }

  public List<Comparison> findComparisonsByPrioritisation(Prioritisation prioritisation) {
    return comparisonRepository.findAllByPrioritisation(prioritisation);
  }

  public List<Comparison> findComparisonsByParent(Node parent) {
    return comparisonRepository.findAllByParent(parent);
  }

  public FullResultDTO calculateAHP(Prioritisation prioritisation, List<Node> startNodes) {
    List<Comparison> comparisons = comparisonRepository.findAllByPrioritisation(prioritisation);

    // Create a single shared nodeMap to ensure same instances are used everywhere
    java.util.Map<String, de.x132.ahp.core.model.Node> nodeMap = createNodeMap(comparisons);

    // Build the hierarchy from comparisons using shared nodeMap
    List<de.x132.ahp.core.model.Node> coreStartNodes =
        buildHierarchyFromComparisons(startNodes, comparisons, nodeMap);

    // Convert to core model using shared nodeMap
    de.x132.ahp.core.model.Prioritisation corePrioritisation =
        convertToCorePrioritisation(prioritisation, comparisons, nodeMap);

    return solver.getSolvingResult(coreStartNodes, corePrioritisation);
  }

  private java.util.Map<String, de.x132.ahp.core.model.Node> createNodeMap(
      List<Comparison> comparisons) {
    // Create a map to store all nodes by name
    java.util.Map<String, de.x132.ahp.core.model.Node> nodeMap = new java.util.HashMap<>();

    // Collect all unique nodes from comparisons
    for (Comparison comparison : comparisons) {
      String parentName = comparison.getParent().getName();
      String leftName = comparison.getLeftNode().getName();
      String rightName = comparison.getRightNode().getName();

      nodeMap.putIfAbsent(
          parentName, de.x132.ahp.core.model.Node.builder().name(parentName).build());
      nodeMap.putIfAbsent(leftName, de.x132.ahp.core.model.Node.builder().name(leftName).build());
      nodeMap.putIfAbsent(rightName, de.x132.ahp.core.model.Node.builder().name(rightName).build());
    }

    return nodeMap;
  }

  private List<de.x132.ahp.core.model.Node> buildHierarchyFromComparisons(
      List<Node> startNodes,
      List<Comparison> comparisons,
      java.util.Map<String, de.x132.ahp.core.model.Node> nodeMap) {

    // Build parent-child relationships
    for (Comparison comparison : comparisons) {
      de.x132.ahp.core.model.Node parent = nodeMap.get(comparison.getParent().getName());
      de.x132.ahp.core.model.Node leftChild = nodeMap.get(comparison.getLeftNode().getName());
      de.x132.ahp.core.model.Node rightChild = nodeMap.get(comparison.getRightNode().getName());

      // Add children to parent if not already present
      if (!parent.getChildren().contains(leftChild)) {
        parent.getChildren().add(leftChild);
        leftChild.getParents().add(parent);
      }
      if (!parent.getChildren().contains(rightChild)) {
        parent.getChildren().add(rightChild);
        rightChild.getParents().add(parent);
      }
    }

    // Return the start nodes
    return startNodes.stream()
        .map(node -> nodeMap.get(node.getName()))
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private de.x132.ahp.core.model.Prioritisation convertToCorePrioritisation(
      Prioritisation prioritisation,
      List<Comparison> comparisons,
      java.util.Map<String, de.x132.ahp.core.model.Node> nodeMap) {

    de.x132.ahp.core.model.Project coreProject =
        de.x132.ahp.core.model.Project.builder()
            .name(prioritisation.getProject().getName())
            .build();

    de.x132.ahp.core.model.Prioritisation corePrioritisation =
        de.x132.ahp.core.model.Prioritisation.builder()
            .name(prioritisation.getName())
            .project(coreProject)
            .build();

    // Convert comparisons using the same node instances
    List<de.x132.ahp.core.model.Comparison> coreComparisons =
        comparisons.stream().map(c -> convertToCoreComparison(c, nodeMap)).toList();

    corePrioritisation.setComparisons(coreComparisons);

    return corePrioritisation;
  }

  private de.x132.ahp.core.model.Comparison convertToCoreComparison(
      Comparison comparison, java.util.Map<String, de.x132.ahp.core.model.Node> nodeMap) {

    de.x132.ahp.core.model.Node parentNode = nodeMap.get(comparison.getParent().getName());
    de.x132.ahp.core.model.Node leftNode = nodeMap.get(comparison.getLeftNode().getName());
    de.x132.ahp.core.model.Node rightNode = nodeMap.get(comparison.getRightNode().getName());

    return de.x132.ahp.core.model.Comparison.builder()
        .parent(parentNode)
        .nodeA(leftNode)
        .nodeB(rightNode)
        .value(comparison.getWeight())
        .build();
  }
}
