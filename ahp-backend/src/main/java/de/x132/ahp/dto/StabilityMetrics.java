package de.x132.ahp.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StabilityMetrics {
  private double stabilityScore;
  private RiskLevel riskLevel;
  private double toleranceRange;
  private int rankingChangeCount;

  public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH
  }
}
