package de.x132.ahp.model.json;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
  private Map<String, Double> criteriaWeights;
  private Map<String, Map<String, Double>>
      alternativeWeightsByCriterion; // criteriaId -> (altId -> weight)
  private Map<String, Double> resultingWeights; // altId -> weight

  // Optional metadata maps often present in the JSON
  private Map<String, String> criteriaNames;
  private Map<String, String> alternativeNames;

  // Legacy fields that might be present
  private Object finalResults;
  private Object alternativeScoresPerCriterion;
}
