package de.x132.test.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;

import de.x132.common.business.ahp.transfer.AHPResultDTO;
import de.x132.common.business.transfer.ChildResultDTO;
import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.comparison.transfer.ComparisonDTO;
import de.x132.connection.transfer.ConnectionDTO;
import de.x132.node.transfer.NodeDTO;
import de.x132.prioritisation.transfer.PrioritisationDTO;
import de.x132.project.transfer.ProjectDTO;
import de.x132.user.transfer.UserDTO;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http.Status;

public class TestClient {

	WSClient client;
	private String currentToken;

	public TestClient(int i) {
		client = WS.newClient(i);
	}

	public void setCompareWeight(ProjectDTO projectDTO, PrioritisationDTO prioritisation, NodeDTO parent,
			NodeDTO left, NodeDTO right, int weight) throws InterruptedException, ExecutionException{
		int index = 1;
		if(weight==1 || weight==-1){
			index =0;
		}
		if(weight==3 || weight==-3){
			index =1;
		}
		if(weight==5 || weight==-5){
			index=2;
		}
		if(weight==7 || weight==-7){
			index=3;
		}
		if(weight==9 || weight==-9){
			index=4;
		}
		index = weight < 0 ? index*-1 : index;
		this.setCompareValue(projectDTO, prioritisation, parent, left, right, index);
	}
	
	public void setCompareValue(ProjectDTO projectDTO, PrioritisationDTO prioritisation, NodeDTO parent,
			NodeDTO left, NodeDTO right, int weight) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage;
		WSResponse response;
		completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/" + prioritisation.getName()
						+ "/comparisons/" + parent.getName() + "/" + left.getName() + "/" + right.getName() + "/")
				.setHeader("authorization", "Basic " + this.currentToken).get();
		response = completionStage.toCompletableFuture().get();
		JsonNode parse = Json.parse(response.getBody());
		ComparisonDTO comparison = Json.fromJson(parse, ComparisonDTO.class);

		if(comparison.getRightNodeName().equals( right.getName())){
			comparison.setWeight(weight);
		} else {
			comparison.setWeight(weight*-1);
		}
		assertThat(comparison.getParentNodeName(), equalTo(parent.getName()));

		completionStage = client.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"
				+ prioritisation.getName() + "/comparisons/").setHeader("authorization", "Basic " + this.currentToken)
				.put(Json.toJson(comparison));
		response = completionStage.toCompletableFuture().get();

		assertEquals(Status.OK, response.getStatus());
	}

	public PrioritisationDTO checkPrioritysation(ProjectDTO projectDTO, PrioritisationDTO prioritisation, int assertionSize)
			throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage;
		WSResponse response;
		completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/" + prioritisation.getName())
				.setHeader("authorization", "Basic " + this.currentToken).get();
		response = completionStage.toCompletableFuture().get();

		assertEquals(Status.OK, response.getStatus());

		JsonNode parse = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(parse));
		
		PrioritisationDTO fromJson = Json.fromJson(parse, PrioritisationDTO.class);
		assertThat(fromJson.getName(), equalTo("test"));
		assertThat(fromJson.getComparisons().size(), equalTo(assertionSize));
		return fromJson;
	}

	public PrioritisationDTO addPrioritisation(ProjectDTO projectDTO, PrioritisationDTO prioritisation)
			throws InterruptedException, ExecutionException {
		JsonNode json = Json.toJson(prioritisation);
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/")
				.setHeader("authorization", "Basic " + this.currentToken).post(json);
		WSResponse response = completionStage.toCompletableFuture().get();

		assertEquals(Status.OK, response.getStatus());
		return prioritisation;
	}

	public void addConnection(ProjectDTO projectDTO, NodeDTO nodeA, NodeDTO nodeB)
			throws InterruptedException, ExecutionException {
		ConnectionDTO connectionAB = new ConnectionDTO();
		connectionAB.setSourcenode(nodeA.getName());
		connectionAB.setTargetnode(nodeB.getName());
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/connections/")
				.setHeader("authorization", "Basic " + this.currentToken).post(Json.toJson(connectionAB));
		WSResponse response;
		response = completionStage.toCompletableFuture().get();

		assertEquals(Status.OK, response.getStatus());
	}

	public void addNode(ProjectDTO projectDTO, NodeDTO nodeA)
			throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/nodes/")
				.setHeader("authorization", "Basic " + this.currentToken).post(Json.toJson(nodeA));
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
	}

	public void addProject(ProjectDTO projectDTO) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client.url("/rest/api/1/projects/")
				.setHeader("authorization", "Basic " + this.currentToken).post(Json.toJson(projectDTO));
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
	}
	
	public AHPResultDTO getResultFor(ProjectDTO projectDTO, PrioritisationDTO prioritisation, NodeDTO nodeA) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"+prioritisation.getName()+"/results/"+nodeA.getName())
				.setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		
		JsonNode parse = response.asJson();
		JsonNode toPars = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(toPars));
		return Json.fromJson(parse, AHPResultDTO.class);
	}
	
	public ChildResultDTO getChildResult(ProjectDTO projectDTO, PrioritisationDTO prioritisation, NodeDTO node) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"+prioritisation.getName()+"/results/"+node.getName()+"/childs")
				.setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		JsonNode parse = response.asJson();
		JsonNode toPars = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(toPars));
		return Json.fromJson(parse, ChildResultDTO.class);
	}
	
	public SolvingResultDTO getInfluenceWeights(ProjectDTO projectDTO, PrioritisationDTO prioritisation, NodeDTO node) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"+prioritisation.getName()+"/influences/"+node.getName())
				.setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());

		JsonNode parse = response.asJson();
		JsonNode toPars = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(toPars));
		return Json.fromJson(parse, SolvingResultDTO.class);
	}
	
	public FullResultDTO getFullResult(ProjectDTO projectDTO, PrioritisationDTO prioritisation) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"+prioritisation.getName()+"/fullresult")
				.setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		JsonNode parse = response.asJson();
		JsonNode toPars = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(toPars));
		return Json.fromJson(parse, FullResultDTO.class);
	}
	
	public FullResultDTO getResultForLeafs(ProjectDTO projectDTO, PrioritisationDTO prioritisation) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client
				.url("/rest/api/1/projects/" + projectDTO.getName() + "/prioritisations/"+prioritisation.getName()+"/result")
				.setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		JsonNode parse = response.asJson();
		JsonNode toPars = Json.parse(response.getBody());
		System.out.println(Json.prettyPrint(toPars));
		return Json.fromJson(parse, FullResultDTO.class);
	}


	public void authenticate(UserDTO user1)
			throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		String authentication = this.generateAuth(user1.getNickname(), user1.getPassword());
		CompletionStage<WSResponse> completionStage = client.url("/rest/api/1/users/login/")
				.setHeader("authorization", authentication).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		assertThat(response.getBody().length(), equalTo(16));

		this.currentToken = response.getBody();
	}

	public void registerUser(UserDTO user1) throws InterruptedException, ExecutionException {
		CompletionStage<WSResponse> completionStage = client.url("/rest/api/1/users/").post(Json.toJson(user1));
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
	}

	public void disconnect() throws IOException, InterruptedException, ExecutionException {
        // Logout
		CompletionStage<WSResponse> completionStage = this.client.url("/rest/api/1/users/logout/").setHeader("authorization", "Basic " + this.currentToken).get();
		WSResponse response = completionStage.toCompletableFuture().get();
		assertEquals(Status.OK, response.getStatus());
		
		this.client.close();		
	}
	
	private String generateAuth(String nickname, String password) throws UnsupportedEncodingException {
		String comulatedString = nickname+":"+password;
		byte[] decode = Base64.encodeBase64(comulatedString.getBytes());
		String decodedString = new String(decode, "UTF-8");
		String basicAuth = "Basic " + decodedString;
		return basicAuth;
	}


}
