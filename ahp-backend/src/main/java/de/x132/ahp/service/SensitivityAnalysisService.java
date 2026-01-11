package de.x132.ahp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.x132.ahp.dto.*;
import de.x132.ahp.dto.StabilityMetrics.RiskLevel;
import de.x132.ahp.model.Analysis;
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

  private final ObjectMapper objectMapper;
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
      // Parse results JSON
      Map<String, Object> results =
          objectMapper.readValue(
              analysis.getResults(), new TypeReference<Map<String, Object>>() {});

      Map<String, String> criterionNames = new HashMap<>();
      Map<Long, String> alternativeNames = new HashMap<>();

      Map<String, Double> criteriaWeights = extractCriteriaWeights(results, criterionNames);
      Map<String, Map<String, Double>> alternativeWeights =
          extractAlternativeWeights(results, criterionNames, alternativeNames);

      // Fallback: also take names from final results if available
      alternativeNames.putAll(getAlternativeNames(results));

      String criterionKey = String.valueOf(criterionId);
      String criterionName =
          criterionNames.getOrDefault(criterionKey, getCriterionName(results, criterionKey));
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
    double originalWeight = originalWeights.getOrDefault(targetCriterion, 0.0);

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
    double remainingWeight = 1.0 - newWeight;
    double originalRemainingWeight =
        originalWeights.entrySet().stream()
            .filter(e -> !e.getKey().equals(targetCriterion))
            .mapToDouble(Map.Entry::getValue)
            .sum();

    // Proportionally adjust other weights
    if (originalRemainingWeight > 0) {
      for (Map.Entry<String, Double> entry : originalWeights.entrySet()) {
        if (!entry.getKey().equals(targetCriterion)) {
          double proportion = entry.getValue() / originalRemainingWeight;
          adjusted.put(entry.getKey(), proportion * remainingWeight);
        }
      }
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
                    String.format(
                        "At weight %.1f%%, ranking changes from %s to %s",
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

  private Map<String, Double> extractCriteriaWeights(
      Map<String, Object> results, Map<String, String> criterionNames) {
    Map<String, Double> criteriaWeights = new HashMap<>();
    Object rawWeights = results.get("criteriaWeights");

    if (rawWeights instanceof Map<?, ?> mapWeights) {
      mapWeights.forEach(
          (k, v) -> {
            if (v instanceof Number num) {
              criteriaWeights.put(String.valueOf(k), num.doubleValue());
            }
          });
    } else if (rawWeights instanceof List<?> listWeights) {
      for (Object item : listWeights) {
        if (item instanceof Map<?, ?> entry) {
          Object criterionObj = entry.get("criterion");
          Object weightObj = entry.get("weight");
          if (criterionObj instanceof Map<?, ?> critMap && weightObj instanceof Number num) {
            Object idObj = critMap.get("id");
            Object nameObj = critMap.get("name");
            if (idObj instanceof Number idNum) {
              String key = String.valueOf(idNum.longValue());
              criteriaWeights.put(key, num.doubleValue());
              if (nameObj instanceof String name) {
                criterionNames.put(key, name);
              }
            }
          }
        }
      }
    }

    return criteriaWeights;
  }

  private Map<String, Map<String, Double>> extractAlternativeWeights(
      Map<String, Object> results,
      Map<String, String> criterionNames,
      Map<Long, String> alternativeNames) {

    Object raw = results.get("alternativeWeightsByCriterion");
    if (raw instanceof Map<?, ?> direct) {
      @SuppressWarnings("unchecked")
      Map<String, Map<String, Double>> casted = (Map<String, Map<String, Double>>) direct;
      return casted;
    }

    Map<String, Map<String, Double>> converted = new HashMap<>();
    Object scoresPerCriterion = results.get("alternativeScoresPerCriterion");

    if (scoresPerCriterion instanceof Map<?, ?> scoresMap) {
      for (Map.Entry<?, ?> entry : scoresMap.entrySet()) {
        String criterionKey = String.valueOf(entry.getKey());
        String criterionId =
            criterionNames.entrySet().stream()
                .filter(e -> e.getValue().equals(criterionKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(criterionKey);

        Map<String, Double> altWeights = new HashMap<>();
        Object altListObj = entry.getValue();
        if (altListObj instanceof List<?> altList) {
          for (Object altEntry : altList) {
            if (altEntry instanceof Map<?, ?> altMap) {
              Object altObj = altMap.get("alternative");
              Object localWeightObj = altMap.get("localWeight");
              if (altObj instanceof Map<?, ?> altInfo && localWeightObj instanceof Number lw) {
                Object altIdObj = altInfo.get("id");
                Object altNameObj = altInfo.get("name");
                if (altIdObj instanceof Number altIdNum) {
                  String altKey = String.valueOf(altIdNum.longValue());
                  altWeights.put(altKey, lw.doubleValue());
                  if (altNameObj instanceof String altName) {
                    alternativeNames.putIfAbsent(Long.parseLong(altKey), altName);
                  }
                }
              }
            }
          }
        }

        converted.put(criterionId, altWeights);
      }
    }

    // Fallback: try to hydrate alternative names from finalResults if not present
    Object finalResults = results.get("finalResults");
    if (finalResults instanceof List<?> list) {
      for (Object item : list) {
        if (item instanceof Map<?, ?> resultMap) {
          Object altObj = resultMap.get("alternative");
          Object scoreObj = resultMap.get("score");
          if (altObj instanceof Map<?, ?> altInfo) {
            Object altIdObj = altInfo.get("id");
            Object altNameObj = altInfo.get("name");
            if (altIdObj instanceof Number altIdNum) {
              String altKey = String.valueOf(altIdNum.longValue());
              if (scoreObj instanceof Number scoreNum) {
                Map<String, Double> byCriterion =
                    converted.computeIfAbsent("__final__", k -> new HashMap<>());
                byCriterion.put(altKey, scoreNum.doubleValue());
              }
              if (altNameObj instanceof String altName) {
                alternativeNames.putIfAbsent(Long.parseLong(altKey), altName);
              }
            }
          }
        }
      }
    }

    return converted;
  }

  private String getCriterionName(Map<String, Object> results, String criterionKey) {
    @SuppressWarnings("unchecked")
    Map<String, String> criteriaNames = (Map<String, String>) results.get("criteriaNames");
    if (criteriaNames != null) {
      return criteriaNames.getOrDefault(criterionKey, "Criterion " + criterionKey);
    }
    return "Criterion " + criterionKey;
  }

  private Map<Long, String> getAlternativeNames(Map<String, Object> results) {
    Map<Long, String> names = new HashMap<>();
    @SuppressWarnings("unchecked")
    Map<String, String> altNames = (Map<String, String>) results.get("alternativeNames");

    if (altNames != null) {
      for (Map.Entry<String, String> entry : altNames.entrySet()) {
        try {
          names.put(Long.parseLong(entry.getKey()), entry.getValue());
        } catch (NumberFormatException e) {
          log.warn("Invalid alternative ID: {}", entry.getKey());
        }
      }
    }

    Object finalResults = results.get("finalResults");
    if (finalResults instanceof List<?> list) {
      for (Object item : list) {
        if (item instanceof Map<?, ?> resultMap) {
          Object altObj = resultMap.get("alternative");
          if (altObj instanceof Map<?, ?> altInfo) {
            Object altIdObj = altInfo.get("id");
            Object altNameObj = altInfo.get("name");
            if (altIdObj instanceof Number altIdNum && altNameObj instanceof String altName) {
              names.putIfAbsent(altIdNum.longValue(), altName);
            }
          }
        }
      }
    }
    return names;
  }
}
