package de.x132.ahp.core.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the complete AHP calculation result. Contains results for all
 * nodes in the hierarchy.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullResultDTO {

  /** List of solving results for all parent nodes in the hierarchy. */
  @Builder.Default private List<SolvingResultDTO> nodeResults = new ArrayList<>();

  /** Overall consistency of the entire hierarchy. */
  private boolean overallConsistent;

  /** Number of inconsistent nodes in the hierarchy. */
  private int inconsistentNodeCount;

  /**
   * Adds a solving result for a specific node.
   *
   * @param result the solving result to add
   */
  public void addNodeResult(SolvingResultDTO result) {
    if (this.nodeResults == null) {
      this.nodeResults = new ArrayList<>();
    }
    this.nodeResults.add(result);

    if (!result.isConsistent()) {
      this.inconsistentNodeCount++;
    }
  }
}
