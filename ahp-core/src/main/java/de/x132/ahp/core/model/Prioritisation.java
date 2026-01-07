package de.x132.ahp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete prioritization with all comparisons.
 * A prioritization contains all pairwise comparisons for a project.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prioritisation {

    /**
     * Unique identifier of the prioritization.
     */
    private Long id;

    /**
     * Name of the prioritization.
     */
    private String name;

    /**
     * List of all comparisons in this prioritization.
     */
    @Builder.Default
    private List<Comparison> comparisons = new ArrayList<>();

    /**
     * The project this prioritization belongs to.
     */
    private Project project;

    /**
     * Adds a comparison to this prioritization.
     *
     * @param comparison the comparison to add
     */
    public void addComparison(Comparison comparison) {
        if (this.comparisons == null) {
            this.comparisons = new ArrayList<>();
        }
        this.comparisons.add(comparison);
    }

    /**
     * Gets all comparisons for a specific parent node.
     *
     * @param parent the parent node
     * @return list of comparisons under this parent
     */
    public List<Comparison> getComparisonsForParent(Node parent) {
        return comparisons.stream()
                .filter(c -> c.getParent().equals(parent))
                .toList();
    }
}
