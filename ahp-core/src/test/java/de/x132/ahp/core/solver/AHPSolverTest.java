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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testFullResultWithMultipleNodes() {
        // Create a more complex hierarchy with goal -> 2 criteria -> 2 alternatives each
        Project complexProject = createComplexProject();
        Prioritisation prioritisation = solver.generatePrioritisation(complexProject);

        // Set some comparison values
        List<Comparison> comparisons = prioritisation.getComparisons();
        setComparisonValue(comparisons, "Criterion1", "Criterion2", BigDecimal.valueOf(2));
        setComparisonValue(comparisons, "Alternative1A", "Alternative1B", BigDecimal.valueOf(3));
        setComparisonValue(comparisons, "Alternative2A", "Alternative2B", BigDecimal.valueOf(2));

        // Get full solving result
        var fullResult = solver.getSolvingResult(
                List.of(complexProject.findNodeByName("Goal")),
                prioritisation
        );

        assertNotNull(fullResult);
        assertTrue(fullResult.getNodeResults().size() >= 2, "Should have results for multiple parent nodes");
    }

    @Test
    void testEmptyProject() {
        Project emptyProject = Project.builder()
                .id(99L)
                .name("Empty Project")
                .build();

        Prioritisation prioritisation = solver.generatePrioritisation(emptyProject);

        assertNotNull(prioritisation);
        assertEquals(0, prioritisation.getComparisons().size(), "Empty project should have no comparisons");
    }

    @Test
    void testSingleNodeProject() {
        Project singleNodeProject = Project.builder()
                .id(99L)
                .name("Single Node Project")
                .build();

        Node singleNode = Node.builder()
                .id(1L)
                .name("OnlyNode")
                .build();

        singleNodeProject.addNode(singleNode);

        Prioritisation prioritisation = solver.generatePrioritisation(singleNodeProject);

        assertNotNull(prioritisation);
        assertEquals(0, prioritisation.getComparisons().size(), "Single node should have no comparisons");
    }

    @Test
    void testNodeWithOneChild() {
        Node parent = Node.builder().name("Parent").build();
        Node child = Node.builder().name("Child").build();
        parent.addChild(child);

        Project project = Project.builder()
                .id(1L)
                .name("One Child Project")
                .build();

        project.addNode(parent);
        project.addNode(child);

        Prioritisation prioritisation = solver.generatePrioritisation(project);

        assertNotNull(prioritisation);
        assertEquals(0, prioritisation.getComparisons().size(), "One child should have no comparisons");
    }

    /**
     * Creates a more complex test project with 2 levels of hierarchy.
     */
    private Project createComplexProject() {
        // Level 0: Goal
        Node goal = Node.builder().id(1L).name("Goal").build();

        // Level 1: Criteria
        Node criterion1 = Node.builder().id(2L).name("Criterion1").build();
        Node criterion2 = Node.builder().id(3L).name("Criterion2").build();

        goal.addChild(criterion1);
        goal.addChild(criterion2);
        criterion1.addParent(goal);
        criterion2.addParent(goal);

        // Level 2: Alternatives under each criterion
        Node alt1A = Node.builder().id(4L).name("Alternative1A").build();
        Node alt1B = Node.builder().id(5L).name("Alternative1B").build();
        Node alt2A = Node.builder().id(6L).name("Alternative2A").build();
        Node alt2B = Node.builder().id(7L).name("Alternative2B").build();

        criterion1.addChild(alt1A);
        criterion1.addChild(alt1B);
        alt1A.addParent(criterion1);
        alt1B.addParent(criterion1);

        criterion2.addChild(alt2A);
        criterion2.addChild(alt2B);
        alt2A.addParent(criterion2);
        alt2B.addParent(criterion2);

        // Create project
        Project project = Project.builder()
                .id(2L)
                .name("Complex Project")
                .build();

        project.addNode(goal);
        project.addNode(criterion1);
        project.addNode(criterion2);
        project.addNode(alt1A);
        project.addNode(alt1B);
        project.addNode(alt2A);
        project.addNode(alt2B);

        return project;
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
