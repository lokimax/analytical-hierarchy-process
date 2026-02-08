package de.x132.ahp.core.solver;

import static org.junit.jupiter.api.Assertions.*;

import de.x132.ahp.core.dto.SingleResult;
import de.x132.ahp.core.model.Comparison;
import de.x132.ahp.core.model.Node;
import de.x132.ahp.core.model.Prioritisation;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for AHPNode - tests the core AHP matrix calculations.
 *
 * @author Max Wick
 */
@DisplayName("AHPNode Tests")
class AHPNodeTest {

  @Test
  @DisplayName("Should calculate correct priorities for equal comparisons")
  void shouldCalculateEqualPriorities() {
    // Given: 3 nodes with equal importance
    Node parent = createNodeWithChildren("Parent", 3);
    Prioritisation prioritisation = createPrioritisationWithEqualWeights(parent);

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    List<SingleResult> results = ahpNode.getSingleResults();
    assertEquals(3, results.size());

    // All priorities should be approximately 1/3
    for (SingleResult result : results) {
      BigDecimal expected = new BigDecimal("0.333");
      BigDecimal actual = result.getValue();
      assertTrue(
          actual.subtract(expected).abs().compareTo(new BigDecimal("0.01")) < 0,
          "Priority should be ~0.333 but was " + actual);
    }
  }

  @Test
  @DisplayName("Should detect consistent comparison matrix")
  void shouldDetectConsistentMatrix() {
    // Given: Consistent comparisons (A:B=2, B:C=3, A:C=6)
    Node parent = Node.builder().name("Parent").build();
    Node child1 = Node.builder().name("A").build();
    Node child2 = Node.builder().name("B").build();
    Node child3 = Node.builder().name("C").build();

    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    Prioritisation prioritisation = Prioritisation.builder().build();

    // Consistent: A:B=2, B:C=3, A:C=6 (2*3=6)
    prioritisation.addComparison(createComparison(parent, child1, child2, BigDecimal.valueOf(2)));
    prioritisation.addComparison(createComparison(parent, child2, child3, BigDecimal.valueOf(3)));
    prioritisation.addComparison(createComparison(parent, child1, child3, BigDecimal.valueOf(6)));

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    assertTrue(ahpNode.isConsistent(), "Matrix should be consistent");
    assertTrue(
        ahpNode.getCr().compareTo(BigDecimal.valueOf(0.1)) <= 0,
        "CR should be <= 0.1 but was " + ahpNode.getCr());
  }

  @Test
  @DisplayName("Should detect inconsistent comparison matrix")
  void shouldDetectInconsistentMatrix() {
    // Given: Inconsistent comparisons (A:B=3, B:C=4, A:C=2 instead of 12)
    Node parent = Node.builder().name("Parent").build();
    Node child1 = Node.builder().name("A").build();
    Node child2 = Node.builder().name("B").build();
    Node child3 = Node.builder().name("C").build();

    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    Prioritisation prioritisation = Prioritisation.builder().build();

    // Inconsistent: A:B=3, B:C=4, but A:C=2 (should be 12 for consistency)
    prioritisation.addComparison(createComparison(parent, child1, child2, BigDecimal.valueOf(3)));
    prioritisation.addComparison(createComparison(parent, child2, child3, BigDecimal.valueOf(4)));
    prioritisation.addComparison(createComparison(parent, child1, child3, BigDecimal.valueOf(2)));

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    assertFalse(ahpNode.isConsistent(), "Matrix should be inconsistent");
    assertTrue(
        ahpNode.getCr().compareTo(BigDecimal.valueOf(0.1)) > 0,
        "CR should be > 0.1 but was " + ahpNode.getCr());
  }

  @Test
  @DisplayName("Should handle two child nodes (always consistent)")
  void shouldHandleTwoChildren() {
    // Given: 2 nodes (always consistent for n=2)
    Node parent = createNodeWithChildren("Parent", 2);
    Prioritisation prioritisation = Prioritisation.builder().build();

    Node child1 = parent.getChildren().getFirst();
    Node child2 = parent.getChildren().get(1);

    // A much more important than B
    prioritisation.addComparison(createComparison(parent, child1, child2, BigDecimal.valueOf(5)));

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    assertTrue(ahpNode.isConsistent(), "Two-node matrix should always be consistent");
    assertEquals(BigDecimal.ZERO, ahpNode.getCr(), "CR should be 0 for n=2");
    assertEquals(2, ahpNode.getN());

    // Verify priorities sum to 1
    BigDecimal sum =
        ahpNode.getSingleResults().stream()
            .map(SingleResult::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    assertTrue(sum.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 0);
  }

  @Test
  @DisplayName("Should calculate correct priorities with extreme weights")
  void shouldHandleExtremeWeights() {
    // Given: Very high importance differences
    Node parent = Node.builder().name("Parent").build();
    Node critical = Node.builder().name("Critical").build();
    Node minor = Node.builder().name("Minor").build();
    Node negligible = Node.builder().name("Negligible").build();

    parent.addChild(critical);
    parent.addChild(minor);
    parent.addChild(negligible);

    Prioritisation prioritisation = Prioritisation.builder().build();

    // Saaty scale max: 9 (extreme importance)
    prioritisation.addComparison(createComparison(parent, critical, minor, BigDecimal.valueOf(9)));
    prioritisation.addComparison(
        createComparison(parent, critical, negligible, BigDecimal.valueOf(9)));
    prioritisation.addComparison(createComparison(parent, minor, negligible, BigDecimal.ONE));

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    List<SingleResult> results = ahpNode.getSingleResults();
    assertEquals(3, results.size());

    // Critical should have highest priority
    SingleResult criticalResult =
        results.stream().filter(r -> r.getNodeName().equals("Critical")).findFirst().orElseThrow();

    assertTrue(
        criticalResult.getValue().compareTo(new BigDecimal("0.8")) > 0,
        "Critical node should have > 80% priority but was " + criticalResult.getValue());
  }

  @Test
  @DisplayName("Should calculate priorities in correct order")
  void shouldCalculatePrioritiesCorrectly() {
    // Given
    Node parent = Node.builder().name("Parent").build();
    Node high = Node.builder().name("High").build();
    Node medium = Node.builder().name("Medium").build();
    Node low = Node.builder().name("Low").build();

    parent.addChild(high);
    parent.addChild(medium);
    parent.addChild(low);

    Prioritisation prioritisation = Prioritisation.builder().build();

    prioritisation.addComparison(createComparison(parent, high, medium, BigDecimal.valueOf(3)));
    prioritisation.addComparison(createComparison(parent, high, low, BigDecimal.valueOf(5)));
    prioritisation.addComparison(createComparison(parent, medium, low, BigDecimal.valueOf(2)));

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);
    List<SingleResult> results = ahpNode.getSingleResults();

    // Then - Results are in child list order (alphabetical: High, Low, Medium)
    assertEquals(3, results.size());

    // Find each result and verify priorities
    SingleResult highResult =
        results.stream().filter(r -> r.getNodeName().equals("High")).findFirst().orElseThrow();
    SingleResult mediumResult =
        results.stream().filter(r -> r.getNodeName().equals("Medium")).findFirst().orElseThrow();
    SingleResult lowResult =
        results.stream().filter(r -> r.getNodeName().equals("Low")).findFirst().orElseThrow();

    // Verify High > Medium > Low
    assertTrue(
        highResult.getValue().compareTo(mediumResult.getValue()) > 0,
        "High should have higher priority than Medium");
    assertTrue(
        mediumResult.getValue().compareTo(lowResult.getValue()) > 0,
        "Medium should have higher priority than Low");

    // Verify sum equals 1
    BigDecimal sum =
        results.stream().map(SingleResult::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    assertTrue(sum.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 0);
  }

  @Test
  @DisplayName("Should calculate correct lambda max")
  void shouldCalculateLambdaMax() {
    // Given
    Node parent = createNodeWithChildren("Parent", 3);
    Prioritisation prioritisation = createPrioritisationWithEqualWeights(parent);

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    assertNotNull(ahpNode.getLambdaMax());
    // For consistent matrix, lambda_max should be close to n
    BigDecimal lambdaMaxValue = ahpNode.getLambdaMax().bigDecimalValue();
    assertTrue(
        lambdaMaxValue
                .subtract(BigDecimal.valueOf(ahpNode.getN()))
                .abs()
                .compareTo(new BigDecimal("0.1"))
            < 0,
        "Lambda max should be close to n for consistent matrix");
  }

  @Test
  @DisplayName("Should normalize matrix correctly")
  void shouldNormalizeMatrix() {
    // Given
    Node parent = createNodeWithChildren("Parent", 3);
    Prioritisation prioritisation = createPrioritisationWithEqualWeights(parent);

    // When
    AHPNode ahpNode = new AHPNode(parent, prioritisation);

    // Then
    assertNotNull(ahpNode.getNormalized());
    assertEquals(3, ahpNode.getNormalized().getRowDimension());
    assertEquals(3, ahpNode.getNormalized().getColumnDimension());

    // Each column in normalized matrix should sum to 1
    assertNotNull(ahpNode.getColumnSums());
  }

  // Helper methods

  private Node createNodeWithChildren(String parentName, int childCount) {
    Node parent = Node.builder().name(parentName).build();

    for (int i = 1; i <= childCount; i++) {
      Node child = Node.builder().name("Child" + i).build();
      parent.addChild(child);
    }

    return parent;
  }

  private Prioritisation createPrioritisationWithEqualWeights(Node parent) {
    Prioritisation prioritisation = Prioritisation.builder().build();
    List<Node> children = parent.getChildren();

    for (int i = 0; i < children.size(); i++) {
      for (int j = i + 1; j < children.size(); j++) {
        Comparison comparison =
            createComparison(parent, children.get(i), children.get(j), BigDecimal.ONE);
        prioritisation.addComparison(comparison);
      }
    }

    return prioritisation;
  }

  private Comparison createComparison(Node parent, Node nodeA, Node nodeB, BigDecimal value) {
    return Comparison.builder().parent(parent).nodeA(nodeA).nodeB(nodeB).value(value).build();
  }
}
