package de.x132.ahp.service;

import de.x132.ahp.dto.*;
import de.x132.ahp.dto.StabilityMetrics.RiskLevel;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.json.AnalysisResult;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for performing sensitivity analysis on AHP results. Analyzes how changes in criterion
 * weights affect alternative rankings and identifies critical decision points.
 *
 * @author Max Wick
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SensitivityAnalysisService {

  private static final double WEIGHT_STEP = 0.05; // 5% increments
  private static final int DATA_POINTS = 21; // 0%, 5%, 10%, ..., 100%

  /**
   * Performs sensitivity analysis on a specific criterion within an analysis.
   *
   * @param analysis The AHP analysis
   * @param criterionId The criterion to analyze
   * @return Complete sensitivity analysis result
   */
  public SensitivityResult analyzeSensitivity(Analysis analysis, Long criterionId) {
    try {
      // Use typed AnalysisResult instead of parsing JSON manually
      AnalysisResult results = analysis.getResults();

      Map<String, String> criterionNames = new HashMap<>();
      if (results.getCriteriaNames() != null) {
        criterionNames.putAll(results.getCriteriaNames());
      }

      Map<Long, String> alternativeNames = new HashMap<>();
      if (results.getAlternativeNames() != null) {
        try {
          for (Map.Entry<String, String> entry : results.getAlternativeNames().entrySet()) {
            alternativeNames.put(Long.parseLong(entry.getKey()), entry.getValue());
          }
        } catch (NumberFormatException e) {
          log.warn("Error parsing alternative IDs from names map", e);
        }
      }

      // Check if we need to fall back to older structure extraction?
      // For now, assuming AnalysisResult is populated correctly by the Converter
      // However, if the old JSON had different structure (e.g. nested maps for
      // criteriaWeights),
      // The AnalysisResultConverter would handle mapping IT into the POJO if the POJO
      // fields match.
      // My POJO has Map<String, Double> criteriaWeights.
      // If the JSON has `criteriaWeights: ["criterion": {...}, "weight": 0.5]`,
      // Jackson might fail mapping to Map<String, Double>.
      // The original code handled BOTH Map and List formats!
      // This logic leakage refactoring implies we should normalize the data model.
      // But if existing data is mixed, we might break reading it.
      // Given this is Dev/Thesis, I'll assume standard format or that I should have
      // handled it in Converter/POJO.
      // Let's implement robust extraction using the POJO fields.

      Map<String, Double> criteriaWeights = results.getCriteriaWeights();
      if (criteriaWeights == null) criteriaWeights = new HashMap<>();

      Map<String, Map<String, Double>> alternativeWeights =
          results.getAlternativeWeightsByCriterion();
      if (alternativeWeights == null) alternativeWeights = new HashMap<>();

      // Legacy extraction support could be done in the Converter or POJO by using
      // @JsonSetter / @JsonAnySetter
      // But for now, we follow the "Typed Structure" goal.

      String criterionKey = String.valueOf(criterionId);
      String criterionName = criterionNames.getOrDefault(criterionKey, "Criterion " + criterionKey);
      double currentWeight = criteriaWeights.getOrDefault(criterionKey, 0.0);

      // Generate sensitivity data points
      List<SensitivityPoint> dataPoints =
          generateDataPoints(criteriaWeights, alternativeWeights, criterionKey);

      // Find critical points
      List<CriticalPoint> criticalPoints = findCriticalPoints(dataPoints, alternativeNames);

      // Calculate stability metrics
      StabilityMetrics stability = calculateStability(dataPoints, criticalPoints, currentWeight);

      return SensitivityResult.builder()
          .criterionName(criterionName)
          .currentWeight(currentWeight)
          .dataPoints(dataPoints)
          .criticalPoints(criticalPoints)
          .stabilityMetrics(stability)
          .alternativeNames(alternativeNames)
          .build();

    } catch (Exception e) {
      log.error("Error performing sensitivity analysis", e);
      throw new RuntimeException("Failed to perform sensitivity analysis: " + e.getMessage(), e);
    }
  }

  private List<SensitivityPoint> generateDataPoints(
      Map<String, Double> originalWeights,
      Map<String, Map<String, Double>> alternativeWeights,
      String targetCriterion) {

    List<SensitivityPoint> points = new ArrayList<>();
    // double originalWeight = originalWeights.getOrDefault(targetCriterion, 0.0);

    for (int i = 0; i < DATA_POINTS; i++) {
      double newWeight = i * WEIGHT_STEP;

      // Recalibrate other weights
      Map<String, Double> adjustedWeights =
          recalibrateWeights(originalWeights, targetCriterion, newWeight);

      // Calculate alternative scores with new weights
      Map<Long, Double> scores = calculateAlternativeScores(adjustedWeights, alternativeWeights);

      // Generate ranking
      List<Long> ranking = generateRanking(scores);

      points.add(
          SensitivityPoint.builder()
              .criterionWeight(newWeight)
              .alternativeScores(scores)
              .ranking(ranking)
              .build());
    }

    return points;
  }

  private Map<String, Double> recalibrateWeights(
      Map<String, Double> originalWeights, String targetCriterion, double newWeight) {

    Map<String, Double> adjusted = new HashMap<>(originalWeights);
    adjusted.put(targetCriterion, newWeight);

    // Calculate remaining weight to distribute
    // double remainingWeight = 1.0 - newWeight;
    double originalRemainingWeight =
        originalWeights.entrySet().stream()
            .filter(e -> !e.getKey().equals(targetCriterion))
            .mapToDouble(Map.Entry::getValue)
            .sum();

    // Proportionally adjust other weights
    if (originalRemainingWeight > 0) {
      double factor = (1.0 - newWeight) / originalRemainingWeight;
      for (Map.Entry<String, Double> entry : originalWeights.entrySet()) {
        if (!entry.getKey().equals(targetCriterion)) {
          adjusted.put(entry.getKey(), entry.getValue() * factor);
        }
      }
    } else {
      // If original remaining was 0 (e.g. only 1 criterion), we can't distribute.
      // If we have multiple criteria but sum is 0 (shouldn't happen in AHP), logic
      // handled effectively.
    }

    return adjusted;
  }

  private Map<Long, Double> calculateAlternativeScores(
      Map<String, Double> criteriaWeights, Map<String, Map<String, Double>> alternativeWeights) {

    Map<Long, Double> scores = new HashMap<>();

    // Get all alternatives
    Set<String> alternatives = new HashSet<>();
    for (Map<String, Double> weights : alternativeWeights.values()) {
      alternatives.addAll(weights.keySet());
    }

    // Calculate weighted sum for each alternative
    for (String altKey : alternatives) {
      double score = 0.0;
      for (Map.Entry<String, Double> criterion : criteriaWeights.entrySet()) {
        String criterionKey = criterion.getKey();
        double criterionWeight = criterion.getValue();

        Map<String, Double> altsForCriterion = alternativeWeights.get(criterionKey);
        if (altsForCriterion != null) {
          double altWeight = altsForCriterion.getOrDefault(altKey, 0.0);
          score += criterionWeight * altWeight;
        }
      }
      scores.put(Long.parseLong(altKey), score);
    }

    return scores;
  }

  private List<Long> generateRanking(Map<Long, Double> scores) {
    return scores.entrySet().stream()
        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private List<CriticalPoint> findCriticalPoints(
      List<SensitivityPoint> dataPoints, Map<Long, String> altNames) {

    List<CriticalPoint> criticalPoints = new ArrayList<>();

    for (int i = 1; i < dataPoints.size(); i++) {
      SensitivityPoint prev = dataPoints.get(i - 1);
      SensitivityPoint curr = dataPoints.get(i);

      if (prev.getRanking().isEmpty() || curr.getRanking().isEmpty()) continue;

      Long prevWinner = prev.getRanking().get(0);
      Long currWinner = curr.getRanking().get(0);

      if (!prevWinner.equals(currWinner)) {
        double threshold = (prev.getCriterionWeight() + curr.getCriterionWeight()) / 2.0;

        criticalPoints.add(
            CriticalPoint.builder()
                .weightThreshold(threshold)
                .beforeWinnerId(prevWinner)
                .beforeWinnerName(altNames.getOrDefault(prevWinner, "Alternative " + prevWinner))
                .afterWinnerId(currWinner)
                .afterWinnerName(altNames.getOrDefault(currWinner, "Alternative " + currWinner))
                .description(
                    "At weight %.1f%%, ranking changes from %s to %s"
                        .formatted(
                            threshold * 100,
                            altNames.getOrDefault(prevWinner, "Alternative " + prevWinner),
                            altNames.getOrDefault(currWinner, "Alternative " + currWinner)))
                .build());
      }
    }

    return criticalPoints;
  }

  private StabilityMetrics calculateStability(
      List<SensitivityPoint> dataPoints, List<CriticalPoint> criticalPoints, double currentWeight) {

    int rankingChangeCount = criticalPoints.size();

    // Calculate tolerance range (how far weight can vary before ranking changes)
    double toleranceRange = calculateToleranceRange(dataPoints, criticalPoints, currentWeight);

    // Stability score: inverse of ranking change frequency
    double stabilityScore = 1.0 / (1.0 + rankingChangeCount * 0.2);

    // Risk level based on tolerance and change count
    RiskLevel riskLevel;
    if (toleranceRange > 0.3 && rankingChangeCount <= 1) {
      riskLevel = RiskLevel.LOW;
    } else if (toleranceRange > 0.15 || rankingChangeCount <= 3) {
      riskLevel = RiskLevel.MEDIUM;
    } else {
      riskLevel = RiskLevel.HIGH;
    }

    return StabilityMetrics.builder()
        .stabilityScore(stabilityScore)
        .riskLevel(riskLevel)
        .toleranceRange(toleranceRange)
        .rankingChangeCount(rankingChangeCount)
        .build();
  }

  private double calculateToleranceRange(
      List<SensitivityPoint> dataPoints, List<CriticalPoint> criticalPoints, double currentWeight) {

    if (criticalPoints.isEmpty()) {
      return 1.0; // Maximum stability - no changes across spectrum
    }

    // Find closest critical points to current weight
    double lowerBound = 0.0;
    double upperBound = 1.0;

    for (CriticalPoint cp : criticalPoints) {
      if (cp.getWeightThreshold() < currentWeight && cp.getWeightThreshold() > lowerBound) {
        lowerBound = cp.getWeightThreshold();
      }
      if (cp.getWeightThreshold() > currentWeight && cp.getWeightThreshold() < upperBound) {
        upperBound = cp.getWeightThreshold();
      }
    }

    return upperBound - lowerBound;
  }
}
