package de.x132.ahp.core.solver;

import de.x132.ahp.core.dto.SolvingResultDTO;
import de.x132.ahp.core.model.Comparison;
import de.x132.ahp.core.model.Node;
import de.x132.ahp.core.model.Prioritisation;
import de.x132.ahp.core.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AHP Solver functionality.
 *
 * @author Max Wick
 */
class AHPSolverTest {

    private AHPSolver solver;
    private Project testProject;

    @BeforeEach
    void setUp() {
        solver = new AHPSolver();
        testProject = createTestProject();
    }

    /**
     * Creates a simple test project with 3 criteria under one goal.
     */
    private Project createTestProject() {
        // Create nodes
        Node goal = Node.builder()
                .id(1L)
                .name("Goal")
                .description("Main goal")
                .build();

        Node criterion1 = Node.builder()
                .id(2L)
                .name("Criterion1")
                .description("First criterion")
                .build();

        Node criterion2 = Node.builder()
                .id(3L)
                .name("Criterion2")
                .description("Second criterion")
                .build();

        Node criterion3 = Node.builder()
                .id(4L)
                .name("Criterion3")
                .description("Third criterion")
                .build();

        // Establish relationships
        goal.addChild(criterion1);
        goal.addChild(criterion2);
        goal.addChild(criterion3);

        criterion1.addParent(goal);
        criterion2.addParent(goal);
        criterion3.addParent(goal);

        // Create project
        Project project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("AHP Test Project")
                .build();

        project.addNode(goal);
        project.addNode(criterion1);
        project.addNode(criterion2);
        project.addNode(criterion3);

        return project;
    }

    @Test
    void testGeneratePrioritisation() {
        Prioritisation prioritisation = solver.generatePrioritisation(testProject);

        assertNotNull(prioritisation);
        assertNotNull(prioritisation.getComparisons());
        assertEquals(3, prioritisation.getComparisons().size(), "Should have 3 pairwise comparisons for 3 criteria");
    }

    @Test
    void testAhpNodeConsistencyTrue() {
        Node goal = testProject.findNodeByName("Goal");
        Prioritisation prioritisation = solver.generatePrioritisation(testProject);

        // Set consistent comparison values
        List<Comparison> comparisons = prioritisation.getComparisons();
        setComparisonValue(comparisons, "Criterion1", "Criterion2", BigDecimal.valueOf(1)); // C1 = C2
        setComparisonValue(comparisons, "Criterion1", "Criterion3", BigDecimal.valueOf(4)); // C1 > C3
        setComparisonValue(comparisons, "Criterion2", "Criterion3", BigDecimal.valueOf(4)); // C2 > C3 (consistent: 1*4=4)

        AHPNode ahpNode = new AHPNode(goal, prioritisation);

        assertTrue(ahpNode.isConsistent(), "Node should be consistent");
        assertTrue(ahpNode.getCr().compareTo(BigDecimal.valueOf(0.1)) <= 0);
    }

    @Test
    void testAhpNodeConsistencyFalse() {
        Node goal = testProject.findNodeByName("Goal");
        Prioritisation prioritisation = solver.generatePrioritisation(testProject);

        // Set inconsistent comparison values: C1:C2=3, C2:C3=4, but C1:C3=2 (should be 12 for consistency)
        List<Comparison> comparisons = prioritisation.getComparisons();
        setComparisonValue(comparisons, "Criterion1", "Criterion2", BigDecimal.valueOf(3));
        setComparisonValue(comparisons, "Criterion2", "Criterion3", BigDecimal.valueOf(4));
        setComparisonValue(comparisons, "Criterion1", "Criterion3", BigDecimal.valueOf(2)); // Inconsistent!

        AHPNode ahpNode = new AHPNode(goal, prioritisation);

        assertFalse(ahpNode.isConsistent(), "Node should be inconsistent");
        assertTrue(ahpNode.getCr().compareTo(BigDecimal.valueOf(0.1)) > 0);
    }

    @Test
    void testGetSolvingResultFor() {
        Node goal = testProject.findNodeByName("Goal");
        Prioritisation prioritisation = solver.generatePrioritisation(testProject);

        // Set comparison values - consistent values
        List<Comparison> comparisons = prioritisation.getComparisons();
        setComparisonValue(comparisons, "Criterion1", "Criterion2", BigDecimal.ONE);
        setComparisonValue(comparisons, "Criterion1", "Criterion3", BigDecimal.valueOf(4));
        setComparisonValue(comparisons, "Criterion2", "Criterion3", BigDecimal.valueOf(4)); // Consistent with above

        SolvingResultDTO result = solver.getSolvingResultFor(goal, prioritisation);

        assertNotNull(result);
        assertEquals("Goal", result.getParentNodeName());
        assertTrue(result.isConsistent());
        assertEquals(3, result.getResults().size());

        // Verify sum of priorities equals 1 (with small tolerance for floating point)
        BigDecimal sum = result.getResults().stream()
                .map(r -> r.getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertTrue(sum.subtract(BigDecimal.ONE).abs().compareTo(BigDecimal.valueOf(0.0001)) < 0, 
                "Sum of priorities should equal 1, but was " + sum);
    }

    /**
     * Helper method to set comparison value between two nodes.
     */
    private void setComparisonValue(List<Comparison> comparisons, String nodeAName, String nodeBName, BigDecimal value) {
        comparisons.stream()
                .filter(c -> (c.getNodeA().getName().equals(nodeAName) && c.getNodeB().getName().equals(nodeBName))
                        || (c.getNodeA().getName().equals(nodeBName) && c.getNodeB().getName().equals(nodeAName)))
                .findFirst()
                .ifPresent(c -> {
                    if (c.getNodeA().getName().equals(nodeAName)) {
                        c.setValue(value);
                    } else {
                        c.setValue(BigDecimal.ONE.divide(value, 10, RoundingMode.HALF_UP));
                    }
                });
    }
}
