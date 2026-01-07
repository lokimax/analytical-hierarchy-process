package de.x132.ahp.core.solver;

import de.x132.ahp.core.dto.FullResultDTO;
import de.x132.ahp.core.dto.SingleResult;
import de.x132.ahp.core.dto.SolvingResultDTO;
import de.x132.ahp.core.model.Comparison;
import de.x132.ahp.core.model.Node;
import de.x132.ahp.core.model.Prioritisation;
import de.x132.ahp.core.model.Project;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the AHP Solver using the Analytic Hierarchy Process method.
 * This solver creates prioritizations and calculates priorities for hierarchical decision problems.
 *
 * @author Max Wick
 */
@Slf4j
public class AHPSolver implements Solver {

    @Override
    public Prioritisation generatePrioritisation(Project project) {
        Prioritisation prioritisation = Prioritisation.builder()
                .name("Generated Prioritisation for " + project.getName())
                .project(project)
                .build();

        // Generate all necessary pairwise comparisons
        for (Node parentNode : project.getNodes()) {
            List<Node> children = parentNode.getChildren();

            if (children != null && children.size() > 1) {
                // Create pairwise comparisons for all child combinations
                for (int i = 0; i < children.size(); i++) {
                    for (int j = i + 1; j < children.size(); j++) {
                        Node nodeA = children.get(i);
                        Node nodeB = children.get(j);

                        Comparison comparison = Comparison.builder()
                                .parent(parentNode)
                                .nodeA(nodeA)
                                .nodeB(nodeB)
                                .value(BigDecimal.ONE) // Default to equal importance
                                .build();

                        prioritisation.addComparison(comparison);
                    }
                }
            }
        }

        return prioritisation;
    }

    @Override
    public SolvingResultDTO getSolvingResultFor(Node node, Prioritisation prioritisation) {
        AHPNode ahpNode = new AHPNode(node, prioritisation);

        SolvingResultDTO result = SolvingResultDTO.builder()
                .parentNodeName(node.getName())
                .consistent(ahpNode.isConsistent())
                .consistencyRatio(ahpNode.getCr())
                .results(ahpNode.getSingleResults())
                .build();

        if (!ahpNode.isConsistent()) {
            log.warn("Node {} is inconsistent with CR={}", node.getName(), ahpNode.getCr());
        }

        return result;
    }

    @Override
    public FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation) {
        FullResultDTO fullResult = FullResultDTO.builder().build();

        // Calculate results for all nodes that have children
        List<Node> allNodes = collectAllNodes(startNodes);

        for (Node node : allNodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                SolvingResultDTO nodeResult = getSolvingResultFor(node, prioritisation);
                fullResult.addNodeResult(nodeResult);
            }
        }

        // Determine overall consistency
        fullResult.setOverallConsistent(fullResult.getInconsistentNodeCount() == 0);

        return fullResult;
    }

    @Override
    public SolvingResultDTO getInfluenceResult(Node node, Prioritisation prioritisation) {
        // Find all nodes where the given node is a child
        List<Node> parentNodes = node.getParents();

        SolvingResultDTO influenceResult = SolvingResultDTO.builder()
                .parentNodeName(node.getName())
                .consistent(true)
                .build();

        // Calculate influence from each parent
        for (Node parent : parentNodes) {
            AHPNode ahpNode = new AHPNode(parent, prioritisation);
            SingleResult priority = ahpNode.getPriorityFor(node);
            influenceResult.addResult(SingleResult.of(parent.getName(), priority.getValue()));
        }

        return influenceResult;
    }

    @Override
    public FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation, List<Node> showOnlyNodes) {
        FullResultDTO fullResult = getSolvingResult(startNodes, prioritisation);

        // Filter results to show only specified nodes
        List<SolvingResultDTO> filteredResults = fullResult.getNodeResults().stream()
                .filter(result -> showOnlyNodes.stream()
                        .anyMatch(node -> node.getName().equals(result.getParentNodeName())))
                .collect(Collectors.toList());

        fullResult.setNodeResults(filteredResults);
        return fullResult;
    }

    /**
     * Recursively collects all nodes in the hierarchy.
     *
     * @param nodes the starting nodes
     * @return list of all nodes including children
     */
    private List<Node> collectAllNodes(List<Node> nodes) {
        List<Node> allNodes = new ArrayList<>(nodes);

        for (Node node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                List<Node> childNodes = collectAllNodes(node.getChildren());
                for (Node child : childNodes) {
                    if (!allNodes.contains(child)) {
                        allNodes.add(child);
                    }
                }
            }
        }

        return allNodes;
    }
}
