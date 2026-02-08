package de.x132.ahp.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.x132.ahp.dto.SensitivityPoint;
import de.x132.ahp.dto.SensitivityResult;
import de.x132.ahp.dto.StabilityMetrics;
import de.x132.ahp.dto.StabilityMetrics.RiskLevel;
import de.x132.ahp.model.Analysis;
import de.x132.ahp.model.json.AnalysisResult;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for SensitivityAnalysisService.
 *
 * @author Max Wick
 */
class SensitivityAnalysisServiceTest {

  private static final Long CRITERION_ID = 1L;

  private SensitivityAnalysisService service;
  private Analysis analysis;

  @BeforeEach
  void setUp() throws Exception {
    service = new SensitivityAnalysisService();

    AnalysisResult analysisResult =
        AnalysisResult.builder()
            .criteriaWeights(Map.of("1", 0.5, "2", 0.5))
            .alternativeWeightsByCriterion(
                Map.of(
                    "1", Map.of("101", 0.7, "102", 0.3),
                    "2", Map.of("101", 0.4, "102", 0.6)))
            .resultingWeights(Map.of("101", 0.55, "102", 0.45))
            .criteriaNames(Map.of("1", "Price", "2", "Quality"))
            .alternativeNames(Map.of("101", "Supplier A", "102", "Supplier B"))
            .build();

    analysis = Analysis.builder().id(99L).name("demo").results(analysisResult).build();
  }

  @Test
  @DisplayName("analyzeSensitivity returns 21 data points with correct metadata")
  void analyzeSensitivity_returnsDataPoints() {
    SensitivityResult result = service.analyzeSensitivity(analysis, CRITERION_ID);

    assertThat(result.getCriterionName()).isEqualTo("Price");
    assertThat(result.getCurrentWeight()).isEqualTo(0.5);
    assertThat(result.getDataPoints()).hasSize(21);
    assertThat(result.getDataPoints().getFirst().getCriterionWeight()).isZero();
    assertThat(result.getDataPoints().getLast().getCriterionWeight()).isEqualTo(1.0);
  }

  @Test
  @DisplayName("data points contain scores and rankings for all alternatives")
  void dataPointsContainScoresAndRanking() {
    SensitivityResult result = service.analyzeSensitivity(analysis, CRITERION_ID);

    for (SensitivityPoint point : result.getDataPoints()) {
      assertThat(point.getAlternativeScores().keySet()).containsExactlyInAnyOrder(101L, 102L);
      assertThat(point.getRanking()).containsExactlyInAnyOrder(101L, 102L);
    }
  }

  @Test
  @DisplayName("stability metrics are present and within bounds")
  void stabilityMetricsWithinBounds() {
    StabilityMetrics stability =
        service.analyzeSensitivity(analysis, CRITERION_ID).getStabilityMetrics();

    assertThat(stability.getStabilityScore()).isBetween(0.0, 1.0);
    assertThat(stability.getToleranceRange()).isBetween(0.0, 1.0);
    assertThat(stability.getRiskLevel()).isIn(RiskLevel.LOW, RiskLevel.MEDIUM, RiskLevel.HIGH);
  }

  @Test
  @DisplayName("alternative names are forwarded from results JSON")
  void alternativeNamesForwarded() {
    SensitivityResult result = service.analyzeSensitivity(analysis, CRITERION_ID);
    assertThat(result.getAlternativeNames()).containsEntry(101L, "Supplier A");
  }

  @Test
  @DisplayName("critical points thresholds stay within [0,1]")
  void criticalPointsWithinBounds() {
    SensitivityResult result = service.analyzeSensitivity(analysis, CRITERION_ID);
    result
        .getCriticalPoints()
        .forEach(cp -> assertThat(cp.getWeightThreshold()).isBetween(0.0, 1.0));
  }
}
