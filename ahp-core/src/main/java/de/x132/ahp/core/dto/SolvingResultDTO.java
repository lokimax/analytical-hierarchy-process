package de.x132.ahp.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for AHP solving results.
 * Contains a list of single results representing priorities for multiple nodes.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolvingResultDTO {

    /**
     * List of individual node results.
     */
    @Builder.Default
    private List<SingleResult> results = new ArrayList<>();

    /**
     * The parent node name for which this result was calculated.
     */
    private String parentNodeName;

    /**
     * Indicates whether the calculation is consistent (CR <= 0.1).
     */
    private boolean consistent;

    /**
     * Consistency Ratio (CR) value.
     */
    private BigDecimal consistencyRatio;

    /**
     * Adds a single result to this solving result.
     *
     * @param result the single result to add
     */
    public void addResult(SingleResult result) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(result);
    }
}
