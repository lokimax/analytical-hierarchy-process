package de.x132.ahp.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Represents a single data point in the sensitivity analysis curve. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitivityPoint {

  /** The weight of the analyzed criterion at this data point (0.0 - 1.0). */
  private double criterionWeight;

  /** Calculated scores per alternative for this weight. */
  private Map<Long, Double> alternativeScores;

  /** Ranking of alternative IDs sorted descending by score. */
  private List<Long> ranking;
}
