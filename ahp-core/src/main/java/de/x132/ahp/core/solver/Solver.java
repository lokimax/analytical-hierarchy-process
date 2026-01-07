package de.x132.ahp.core.solver;

import de.x132.ahp.core.dto.FullResultDTO;
import de.x132.ahp.core.dto.SolvingResultDTO;
import de.x132.ahp.core.model.Node;
import de.x132.ahp.core.model.Prioritisation;
import de.x132.ahp.core.model.Project;

import java.util.List;

/**
 * Interface for AHP solving algorithms.
 * Defines methods for generating prioritizations and calculating results.
 *
 * @author Max Wick
 */
public interface Solver {

    /**
     * Generates a prioritization template for a project.
     * Creates all necessary pairwise comparisons based on the project structure.
     *
     * @param project the project to generate prioritization for
     * @return a prioritization template with all required comparisons
     */
    Prioritisation generatePrioritisation(Project project);

    /**
     * Calculates the solving result for a specific parent node.
     *
     * @param node           the parent node to calculate results for
     * @param prioritisation the prioritization containing all comparisons
     * @return solving result with priorities for all child nodes
     */
    SolvingResultDTO getSolvingResultFor(Node node, Prioritisation prioritisation);

    /**
     * Calculates the complete solving result for all nodes starting from given start nodes.
     *
     * @param startNodes     list of start nodes to begin calculation from
     * @param prioritisation the prioritization containing all comparisons
     * @return full result with priorities for all nodes in the hierarchy
     */
    FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation);

    /**
     * Calculates influence results (reverse dependencies) for a given node.
     * Shows which nodes influence the given node and by how much.
     *
     * @param node           the node to calculate influences for
     * @param prioritisation the prioritization containing all comparisons
     * @return solving result with influence factors
     */
    SolvingResultDTO getInfluenceResult(Node node, Prioritisation prioritisation);

    /**
     * Calculates solving result filtered to show only specific nodes.
     *
     * @param startNodes     list of start nodes to begin calculation from
     * @param prioritisation the prioritization containing all comparisons
     * @param showOnlyNodes  list of nodes to include in the filtered result
     * @return full result filtered to the specified nodes
     */
    FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation, List<Node> showOnlyNodes);
}
