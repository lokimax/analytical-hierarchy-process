package de.x132.ahp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a pairwise comparison between two nodes in AHP.
 * The comparison value indicates the relative importance or preference.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comparison {

    /**
     * Unique identifier of the comparison.
     */
    private Long id;

    /**
     * The parent node under which this comparison is made.
     */
    private Node parent;

    /**
     * The first node being compared.
     */
    private Node nodeA;

    /**
     * The second node being compared.
     */
    private Node nodeB;

    /**
     * The comparison value (1-9 scale in AHP).
     * Values > 1 mean nodeA is preferred over nodeB.
     * Values < 1 mean nodeB is preferred over nodeA.
     * Value = 1 means equal importance.
     */
    private BigDecimal value;

    /**
     * Creates a comparison with the given nodes and value.
     *
     * @param parent the parent node
     * @param nodeA  the first node
     * @param nodeB  the second node
     * @param value  the comparison value
     * @return a new Comparison instance
     */
    public static Comparison of(Node parent, Node nodeA, Node nodeB, BigDecimal value) {
        return Comparison.builder()
                .parent(parent)
                .nodeA(nodeA)
                .nodeB(nodeB)
                .value(value)
                .build();
    }

    /**
     * Gets the reciprocal comparison (swapping nodeA and nodeB).
     *
     * @return the reciprocal comparison value
     */
    public BigDecimal getReciprocalValue() {
        return BigDecimal.ONE.divide(value, 10, RoundingMode.HALF_UP);
    }
}
