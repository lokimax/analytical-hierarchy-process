package de.x132.ahp.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.x132.ahp.core.dto.FullResultDTO;
import de.x132.ahp.core.dto.SingleResult;
import de.x132.ahp.model.Client;
import de.x132.ahp.model.Comparison;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Prioritisation;
import de.x132.ahp.model.Project;
import de.x132.ahp.model.SolvingMethod;
import de.x132.ahp.model.UserStatus;
import de.x132.ahp.repository.ClientRepository;
import de.x132.ahp.repository.ComparisonRepository;
import de.x132.ahp.repository.NodeRepository;
import de.x132.ahp.repository.PrioritisationRepository;
import de.x132.ahp.repository.ProjectRepository;
import de.x132.ahp.service.PrioritisationService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for the complete AHP algorithm workflow. Tests the full stack from entities
 * through services to the solver.
 *
 * @author Max Wick
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AHPIntegrationTest {

  @Autowired private ClientRepository clientRepository;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private NodeRepository nodeRepository;

  @Autowired private PrioritisationRepository prioritisationRepository;

  @Autowired private ComparisonRepository comparisonRepository;

  @Autowired private PrioritisationService prioritisationService;

  private Client testClient;
  private Project testProject;
  private Node nodeA;
  private Node nodeB;
  private Node nodeC;
  private Node goal;

  @BeforeEach
  public void setup() {
    // Create test client
    testClient =
        Client.builder()
            .nickname("testuser")
            .name("Test")
            .surename("User")
            .email("test@example.com")
            .password("hashedpassword")
            .status(UserStatus.ACTIVE)
            .build();
    testClient = clientRepository.save(testClient);

    // Create test project
    testProject =
        Project.builder()
            .name("AHP Test Project")
            .beschreibung("Test project for AHP integration")
            .client(testClient)
            .build();
    testProject = projectRepository.save(testProject);

    // Create nodes
    goal = Node.builder().name("Goal").content("Main objective").project(testProject).build();
    goal = nodeRepository.save(goal);

    nodeA =
        Node.builder()
            .name("Alternative A")
            .content("First alternative")
            .project(testProject)
            .build();
    nodeA = nodeRepository.save(nodeA);

    nodeB =
        Node.builder()
            .name("Alternative B")
            .content("Second alternative")
            .project(testProject)
            .build();
    nodeB = nodeRepository.save(nodeB);

    nodeC =
        Node.builder()
            .name("Alternative C")
            .content("Third alternative")
            .project(testProject)
            .build();
    nodeC = nodeRepository.save(nodeC);
  }

  @Test
  public void testCompleteAHPWorkflow() {
    // Create prioritisation
    Prioritisation prioritisation =
        Prioritisation.builder()
            .name("Test Prioritisation")
            .method(SolvingMethod.AHP)
            .project(testProject)
            .build();
    prioritisation = prioritisationRepository.save(prioritisation);

    // Add comparisons: A vs B (A is 3 times more important)
    Comparison comp1 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeA)
            .rightNode(nodeB)
            .weight(new BigDecimal("3.0"))
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp1);

    // Add comparisons: A vs C (A is 5 times more important)
    Comparison comp2 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeA)
            .rightNode(nodeC)
            .weight(new BigDecimal("5.0"))
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp2);

    // Add comparisons: B vs C (B is 2 times more important)
    Comparison comp3 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeB)
            .rightNode(nodeC)
            .weight(new BigDecimal("2.0"))
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp3);

    // Calculate AHP result
    List<Node> startNodes = Arrays.asList(goal);
    FullResultDTO result = prioritisationService.calculateAHP(prioritisation, startNodes);

    // Verify results
    assertNotNull(result, "Result should not be null");
    assertNotNull(result.getNodeResults(), "Results list should not be null");
    assertFalse(result.getNodeResults().isEmpty(), "Results should contain elements");

    // Extract single results from the first solving result
    List<SingleResult> singleResults = result.getNodeResults().getFirst().getResults();
    assertNotNull(singleResults, "Single results should not be null");

    // Find priority for each alternative
    SingleResult resultA =
        singleResults.stream()
            .filter(r -> "Alternative A".equals(r.getNodeName()))
            .findFirst()
            .orElse(null);

    SingleResult resultB =
        singleResults.stream()
            .filter(r -> "Alternative B".equals(r.getNodeName()))
            .findFirst()
            .orElse(null);

    SingleResult resultC =
        singleResults.stream()
            .filter(r -> "Alternative C".equals(r.getNodeName()))
            .findFirst()
            .orElse(null);

    assertNotNull(resultA, "Result for Alternative A should exist");
    assertNotNull(resultB, "Result for Alternative B should exist");
    assertNotNull(resultC, "Result for Alternative C should exist");

    // Verify priorities are calculated (exact values depend on AHP algorithm)
    assertNotNull(resultA.getValue(), "Priority for A should not be null");
    assertNotNull(resultB.getValue(), "Priority for B should not be null");
    assertNotNull(resultC.getValue(), "Priority for C should not be null");

    // Verify consistency
    assertTrue(result.isOverallConsistent(), "Result should be consistent");

    // Verify alternative A has highest priority (based on our comparisons)
    assertTrue(
        resultA.getValue().compareTo(resultB.getValue()) > 0,
        "Alternative A should have higher priority than B");
    assertTrue(
        resultA.getValue().compareTo(resultC.getValue()) > 0,
        "Alternative A should have higher priority than C");
    assertTrue(
        resultB.getValue().compareTo(resultC.getValue()) > 0,
        "Alternative B should have higher priority than C");

    // Verify sum of priorities is approximately 1.0
    BigDecimal sum = resultA.getValue().add(resultB.getValue()).add(resultC.getValue());
    assertEquals(0, BigDecimal.ONE.compareTo(sum), "Sum of priorities should be 1.0");
  }

  @Test
  public void testAHPWithEqualWeights() {
    // Create prioritisation
    Prioritisation prioritisation =
        Prioritisation.builder()
            .name("Equal Weights Test")
            .method(SolvingMethod.AHP)
            .project(testProject)
            .build();
    prioritisation = prioritisationRepository.save(prioritisation);

    // Add comparisons with equal weights (all alternatives equally important)
    Comparison comp1 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeA)
            .rightNode(nodeB)
            .weight(BigDecimal.ONE)
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp1);

    Comparison comp2 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeA)
            .rightNode(nodeC)
            .weight(BigDecimal.ONE)
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp2);

    Comparison comp3 =
        Comparison.builder()
            .parent(goal)
            .leftNode(nodeB)
            .rightNode(nodeC)
            .weight(BigDecimal.ONE)
            .prioritisation(prioritisation)
            .build();
    comparisonRepository.save(comp3);

    // Calculate AHP result
    List<Node> startNodes = Arrays.asList(goal);
    FullResultDTO result = prioritisationService.calculateAHP(prioritisation, startNodes);

    // Verify results
    assertNotNull(result, "Result should not be null");
    assertTrue(result.isOverallConsistent(), "Equal weights should always be consistent");

    // Extract single results from the first solving result
    List<SingleResult> singleResults = result.getNodeResults().getFirst().getResults();

    // All alternatives should have equal priority (approximately 1/3)
    for (SingleResult sr : singleResults) {
      if (!sr.getNodeName().equals("Goal")) {
        BigDecimal expected = new BigDecimal("0.333");
        BigDecimal actual = sr.getValue();
        assertTrue(
            actual.subtract(expected).abs().compareTo(new BigDecimal("0.01")) < 0,
            "Priority for " + sr.getNodeName() + " should be approximately 1/3");
      }
    }
  }
}
