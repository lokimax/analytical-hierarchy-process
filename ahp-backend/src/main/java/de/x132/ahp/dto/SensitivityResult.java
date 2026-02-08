package de.x132.ahp.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitivityResult {
  private String criterionName;
  private double currentWeight;
  private List<SensitivityPoint> dataPoints;
  private List<CriticalPoint> criticalPoints;
  private StabilityMetrics stabilityMetrics;
  private Map<Long, String> alternativeNames;
}
