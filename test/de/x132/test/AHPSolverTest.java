package de.x132.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.junit.Test;

import de.x132.common.business.ahp.AHPNode;
import de.x132.common.business.ahp.AHPSolverResultFactory;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.comparison.models.Comparison;
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.project.models.Project;

public class AHPSolverTest {
	
	@Test
	public void testAhpMatrixCreationConsistenceIsFalse(){
		AHPSolverResultFactory solver  = new AHPSolverResultFactory();
		Project project = new ProjectBuilder("Test")
				.addNode("test1")
				.addNode("test2")
				.addNode("test3")
				.addNode("test4")
				.addConnection("test1", "test2")
				.addConnection("test1", "test3")
				.addConnection("test1", "test4")
				.build();
		Prioritisation prioritisation = solver.generatePriorisation(project);
		Iterator<Comparison> iterator = prioritisation.getComparisons().iterator();
		
		while (iterator.hasNext()) {
			Comparison comparison = (Comparison) iterator.next();
			setThen(comparison, "test2", "test3", 3);
			setThen(comparison, "test2", "test4", 4);
			setThen(comparison, "test3", "test4", 2);
		}
		
		Node test1 = project.getNodes().stream().filter(x -> x.getName().equals("test1")).collect(Collectors.toList()).get(0);
		AHPNode ahpNode = new AHPNode(test1, prioritisation);
		assertFalse(ahpNode.isConsistent());
	}
	
	@Test
	public void testAhpMatrixCreationConsistenceIsTrue(){
		AHPSolverResultFactory solver  = new AHPSolverResultFactory();
		Project project = new ProjectBuilder("Test")
				.addNode("test1")
				.addNode("test2")
				.addNode("test3")
				.addNode("test4")
				.addConnection("test1", "test2")
				.addConnection("test1", "test3")
				.addConnection("test1", "test4")
				.build();
		Prioritisation prioritisation = solver.generatePriorisation(project);
		Iterator<Comparison> iterator = prioritisation.getComparisons().iterator();
		
		while (iterator.hasNext()) {
			Comparison comparison = (Comparison) iterator.next();
			setThen(comparison, "test2", "test3", 1);
			setThen(comparison, "test2", "test4", 4);
			setThen(comparison, "test3", "test4", 2);
		}
		
		Node test1 = project.getNodes().stream().filter(x -> x.getName().equals("test1")).collect(Collectors.toList()).get(0);
		AHPNode ahpNode = new AHPNode(test1, prioritisation);
		assertTrue(ahpNode.isConsistent());
	}
	
	@Test
	public void testReverseSolvindForNode(){
		AHPSolverResultFactory solver  = new AHPSolverResultFactory();
		Project project = new ProjectBuilder("Test")
				.addNode("test1")
				.addNode("test2")
				.addNode("test3")
				.addNode("test4")
				.addConnection("test1", "test2")
				.addConnection("test1", "test3")
				.addConnection("test1", "test4")
				.build();
		Prioritisation prioritisation = solver.generatePriorisation(project);
		Iterator<Comparison> iterator = prioritisation.getComparisons().iterator();
		
		while (iterator.hasNext()) {
			Comparison comparison = (Comparison) iterator.next();
			setThen(comparison, "test2", "test3", 1);
			setThen(comparison, "test2", "test4", 4);
			setThen(comparison, "test3", "test4", 2);
		}
		
		Node test3 = project.getNodes().stream().filter(x -> x.getName().equals("test3")).collect(Collectors.toList()).get(0);
		SolvingResultDTO influenceResult = solver.getInfluenceResult(test3, prioritisation);
		
		assertThat(influenceResult.getSingleResults().get(0).getNodename(), equalTo("test1"));
	}
	


	private void setThen(Comparison comparison, String nodeA, String nodeB, int weight) {
		if(comparison.getLeftnode().getName().equals(nodeA) && comparison.getRightnode().getName().equals(nodeB) ||
				comparison.getLeftnode().getName().equals(nodeB) && comparison.getRightnode().getName().equals(nodeA)) {
			comparison.setWeight(weight);
		}
	}
}
