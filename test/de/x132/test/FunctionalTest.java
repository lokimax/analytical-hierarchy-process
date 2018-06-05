package de.x132.test;

import static play.test.Helpers.running;

import java.util.Date;

import org.junit.Test;

import play.test.TestServer;
import de.x132.common.business.SolvingMethod;
import de.x132.node.transfer.NodeDTO;
import de.x132.prioritisation.transfer.PrioritisationDTO;
import de.x132.project.transfer.ProjectDTO;
import de.x132.test.utils.AbstractWiredTest;
import de.x132.test.utils.TestClient;
import de.x132.user.transfer.UserDTO;

public class FunctionalTest extends AbstractWiredTest {

	private static final int TEST_SERVER_HTTP_PORT = 3333;

	@Test
	public void createProjectRoundTrip() throws Exception {
		TestServer server = testServer(TEST_SERVER_HTTP_PORT);
		running(server, () -> {
			try {
				UserDTO user1 = generateUser("User" + String.valueOf(new Date().getTime()));
				TestClient client = new TestClient(TEST_SERVER_HTTP_PORT);

				// Registration
				client.registerUser(user1);

				// Authenticate
				client.authenticate(user1);

				ProjectDTO projectDTO = createProject("TestProject", "Beschreibung zu Projekt");
				client.addProject(projectDTO);

				// Create Node A
				NodeDTO nodeA = createNode("NodeA", "ThisISNodeA");
				client.addNode(projectDTO, nodeA);

				// Create Node B
				NodeDTO nodeB = createNode("NodeB", "ThisISNodeB");
				client.addNode(projectDTO, nodeB);

				// Create Node C
				NodeDTO nodeC = createNode("NodeC", "ThisISNodeC");
				client.addNode(projectDTO, nodeC);

				// Create Node D
				NodeDTO nodeD = createNode("NodeD", "ThisISNodeD");
				client.addNode(projectDTO, nodeD);
				client.addConnection(projectDTO, nodeA, nodeB);
				client.addConnection(projectDTO, nodeA, nodeC);
				client.addConnection(projectDTO, nodeA, nodeD);

				PrioritisationDTO prioritisation = createPrioritisation(SolvingMethod.AHP, "test");
				client.addPrioritisation(projectDTO, prioritisation);

				client.checkPrioritysation(projectDTO, prioritisation, 3);

				client.setCompareValue(projectDTO, prioritisation, nodeA, nodeB, nodeC, 3);
				client.setCompareValue(projectDTO, prioritisation, nodeA, nodeB, nodeD, 4);
				client.setCompareValue(projectDTO, prioritisation, nodeA, nodeC, nodeD, 2);

				client.getResultFor(projectDTO, prioritisation, nodeA);

				client.disconnect();

			} catch (Exception e) {
				getLogger().error(e.getMessage(), e);
			}
		});
	}

	@Test
	public void createNetworkRoundTrip() throws Exception {
		TestServer server = testServer(TEST_SERVER_HTTP_PORT);
		running(server, () -> {
			try {
				UserDTO user1 = generateUser("User" + String.valueOf(new Date().getTime()));
				TestClient client = new TestClient(TEST_SERVER_HTTP_PORT);

				// Registration
				client.registerUser(user1);

				// Authenticate
				client.authenticate(user1);

				ProjectDTO projectDTO = createProject("NetworkTestProject", "Beschreibung zu Projekt");
				client.addProject(projectDTO);

				String[] nodeNames = new String[] { "Goal", "FactorA", "FactorB", "FactorC", "FactorD", "ChoiceX",
						"ChoiceY", "ChoiceZ" };
				// Create Node A
				NodeDTO[] nodes = new NodeDTO[nodeNames.length];
				for (int i = 0; i < nodeNames.length; i++) {
					nodes[i] = createNode(nodeNames[i], "Desc to Node " + nodeNames[i]);
					client.addNode(projectDTO, nodes[i]);
				}
				client.addConnection(projectDTO, nodes[0], nodes[1]);
				client.addConnection(projectDTO, nodes[0], nodes[2]);
				client.addConnection(projectDTO, nodes[0], nodes[3]);
				client.addConnection(projectDTO, nodes[0], nodes[4]);
				client.addConnection(projectDTO, nodes[1], nodes[5]);
				client.addConnection(projectDTO, nodes[1], nodes[6]);
				client.addConnection(projectDTO, nodes[1], nodes[7]);
				client.addConnection(projectDTO, nodes[2], nodes[5]);
				client.addConnection(projectDTO, nodes[2], nodes[6]);
				client.addConnection(projectDTO, nodes[2], nodes[7]);
				client.addConnection(projectDTO, nodes[3], nodes[5]);
				client.addConnection(projectDTO, nodes[3], nodes[6]);
				client.addConnection(projectDTO, nodes[3], nodes[7]);
				client.addConnection(projectDTO, nodes[4], nodes[5]);
				client.addConnection(projectDTO, nodes[4], nodes[6]);
				client.addConnection(projectDTO, nodes[4], nodes[7]);

				PrioritisationDTO prioritisation = createPrioritisation(SolvingMethod.AHP, "test");
				client.addPrioritisation(projectDTO, prioritisation);

				client.checkPrioritysation(projectDTO, prioritisation, 18);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[1], nodes[2], 1);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[1], nodes[3], 3);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[1], nodes[4], 4);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[2], nodes[3], 2);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[2], nodes[4], 3);
				client.setCompareValue(projectDTO, prioritisation, nodes[0], nodes[3], nodes[4], 1);
				client.getResultFor(projectDTO, prioritisation, nodes[0]);

				client.disconnect();

			} catch (Exception e) {
				getLogger().error(e.getMessage(), e);
			}
		});
	}

	@Test
	public void createExampleFromnThesis() throws Exception {
		TestServer server = testServer(TEST_SERVER_HTTP_PORT);
		running(server, () -> {
			try {
				UserDTO user1 = generateUser("User" + String.valueOf(new Date().getTime()));
				TestClient client = new TestClient(TEST_SERVER_HTTP_PORT);

				// Registration
				client.registerUser(user1);

				// Authenticate
				client.authenticate(user1);

				ProjectDTO projectDTO = createProject("ThesisTest", "Architekturbewertung mit AHP");
				client.addProject(projectDTO);

				String[] nodeNames = new String[] { 
						"Ziel1", 			// 0
						"Funktionalitaet", 	// 1
						"Zuverlaessigkeit",  // 2
						"Benutzbarkeit", 	// 3
						"Effizienz", 		// 4
						"Wartbarkeit",		// 5
						"Portabilitaet", 	// 6
						"Szenario1", 		// 7
						"Szenario2", 		// 8
						"Szenario3", 		// 9
						"Szenario4", 		// 10
						"Szenario5", 		// 11
						"Szenario6", 		// 12
						"Szenario7", 		// 13
						"Alternative1", 	// 14
						"Alternative2" };	// 15
				// Create Node A
				NodeDTO[] nodes = new NodeDTO[nodeNames.length];
				for (int i = 0; i < nodeNames.length; i++) {
					nodes[i] = createNode(nodeNames[i], "Desc to Node " + nodeNames[i]);
					client.addNode(projectDTO, nodes[i]);
				}
				// Verbindungen für den Parent Issue
				client.addConnection(projectDTO, nodes[0], nodes[1]);
				client.addConnection(projectDTO, nodes[0], nodes[2]);
				client.addConnection(projectDTO, nodes[0], nodes[3]);
				client.addConnection(projectDTO, nodes[0], nodes[4]);
				client.addConnection(projectDTO, nodes[0], nodes[5]);
				client.addConnection(projectDTO, nodes[0], nodes[6]);
				
				client.addConnection(projectDTO, nodes[1], nodes[8]);
				client.addConnection(projectDTO, nodes[1], nodes[9]);
				client.addConnection(projectDTO, nodes[1], nodes[12]);
				
				client.addConnection(projectDTO, nodes[2], nodes[8]);
				client.addConnection(projectDTO, nodes[2], nodes[11]);
				client.addConnection(projectDTO, nodes[2], nodes[12]);
				
				
				
				client.addConnection(projectDTO, nodes[3], nodes[13]);
				
				// Verbindungen für die Kindissues
				client.addConnection(projectDTO, nodes[4], nodes[8]);
				client.addConnection(projectDTO, nodes[4], nodes[12]);
				
				client.addConnection(projectDTO, nodes[5], nodes[7]);
				client.addConnection(projectDTO, nodes[5], nodes[9]);
				client.addConnection(projectDTO, nodes[5], nodes[10]);

				client.addConnection(projectDTO, nodes[6], nodes[9]);
				
				client.addConnection(projectDTO, nodes[7], nodes[14]);
				client.addConnection(projectDTO, nodes[7], nodes[15]);
				client.addConnection(projectDTO, nodes[8], nodes[14]);
				client.addConnection(projectDTO, nodes[8], nodes[15]);
				client.addConnection(projectDTO, nodes[9], nodes[14]);
				client.addConnection(projectDTO, nodes[9], nodes[15]);
				client.addConnection(projectDTO, nodes[10], nodes[14]);
				client.addConnection(projectDTO, nodes[10], nodes[15]);
				client.addConnection(projectDTO, nodes[11], nodes[14]);
				client.addConnection(projectDTO, nodes[11], nodes[15]);
				client.addConnection(projectDTO, nodes[12], nodes[14]);
				client.addConnection(projectDTO, nodes[12], nodes[15]);
				client.addConnection(projectDTO, nodes[13], nodes[14]);
				client.addConnection(projectDTO, nodes[13], nodes[15]);
				
				PrioritisationDTO prioritisation = createPrioritisation(SolvingMethod.AHP, "test");
				client.addPrioritisation(projectDTO, prioritisation);

				//client.checkPrioritysation(projectDTO, prioritisation, 37);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[1], nodes[2], 1);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[1], nodes[3], 7);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[1], nodes[4], 1);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[1], nodes[5], 3);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[1], nodes[6], 5);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[2], nodes[3], 5);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[2], nodes[4], 1);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[2], nodes[5], 5);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[2], nodes[6], 5);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[3], nodes[4], -5);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[3], nodes[5], -3);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[3], nodes[6], -5);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[4], nodes[5], 5);
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[4], nodes[6], 5);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[0], nodes[5], nodes[6], 3);
				

				client.getResultFor(projectDTO, prioritisation, nodes[0]);
								
						
				client.setCompareWeight(projectDTO, prioritisation, nodes[1], nodes[8], nodes[9], 7);
				client.setCompareWeight(projectDTO, prioritisation, nodes[1], nodes[8], nodes[12], 3);
				client.setCompareWeight(projectDTO, prioritisation, nodes[1], nodes[9], nodes[12], -3);
				
				client.getResultFor(projectDTO, prioritisation, nodes[1]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[2], nodes[8], nodes[11], 7);
				client.setCompareWeight(projectDTO, prioritisation, nodes[2], nodes[8], nodes[12], 3);
				client.setCompareWeight(projectDTO, prioritisation, nodes[2], nodes[11], nodes[12], -5);
				
				client.getResultFor(projectDTO, prioritisation, nodes[2]);
				client.getResultFor(projectDTO, prioritisation, nodes[3]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[4], nodes[8], nodes[12], 5);
				client.getResultFor(projectDTO, prioritisation, nodes[4]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[5], nodes[7], nodes[9], 1);
				client.setCompareWeight(projectDTO, prioritisation, nodes[5], nodes[7], nodes[10], 3);
				client.setCompareWeight(projectDTO, prioritisation, nodes[5], nodes[9], nodes[10], 5);
				
				client.getResultFor(projectDTO, prioritisation, nodes[5]);
				client.getResultFor(projectDTO, prioritisation, nodes[6]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[7], nodes[14], nodes[15], 5);
				client.getResultFor(projectDTO, prioritisation, nodes[7]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[8], nodes[14], nodes[15], 1);
				client.getResultFor(projectDTO, prioritisation, nodes[8]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[9], nodes[14], nodes[15], -3);
				client.getResultFor(projectDTO, prioritisation, nodes[9]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[9], nodes[14], nodes[15], -3);
				client.getResultFor(projectDTO, prioritisation, nodes[9]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[10], nodes[14], nodes[15], 7);
				client.getResultFor(projectDTO, prioritisation, nodes[10]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[11], nodes[14], nodes[15], -7);
				client.getResultFor(projectDTO, prioritisation, nodes[11]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[12], nodes[14], nodes[15], -3);
				client.getResultFor(projectDTO, prioritisation, nodes[12]);
				
				client.setCompareWeight(projectDTO, prioritisation, nodes[13], nodes[14], nodes[15], -7);
				client.getResultFor(projectDTO, prioritisation, nodes[13]);
				
				client.getResultForLeafs(projectDTO, prioritisation);
				client.getChildResult(projectDTO, prioritisation, nodes[0]);
				client.getFullResult(projectDTO, prioritisation);
				
				
				// Authenticate
				client.authenticate(user1);
				
				client.disconnect();				
			} catch (Exception e) {
				getLogger().error(e.getMessage(), e);
			}
		});
	}

	private PrioritisationDTO createPrioritisation(SolvingMethod method, String name) {
		PrioritisationDTO prioritisation = new PrioritisationDTO();
		prioritisation.setMethode(method);
		prioritisation.setName(name);
		return prioritisation;
	}

	private ProjectDTO createProject(String name, String beschreibung) {
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setName(name);
		projectDTO.setBeschreibung(beschreibung);
		return projectDTO;
	}

	private NodeDTO createNode(String name, String beschreibung) {
		NodeDTO nodeA = new NodeDTO();
		nodeA.setName(name);
		nodeA.setBeschreibung(beschreibung);
		return nodeA;
	}

}