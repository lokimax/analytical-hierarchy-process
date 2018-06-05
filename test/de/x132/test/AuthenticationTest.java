package de.x132.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.running;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.CompletionStage;

import org.junit.Test;

import de.x132.test.utils.AbstractWiredTest;
import de.x132.user.transfer.UserDTO;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http.Status;
import play.test.TestServer;

public class AuthenticationTest extends AbstractWiredTest {

	protected String generateAuth(String nickname, String password) throws UnsupportedEncodingException {
		String comulatedString = nickname+":"+password;
		byte[] decode = java.util.Base64.getEncoder().encode(comulatedString.getBytes());
		String decodedString = new String(decode, "UTF-8");
		String basicAuth = "Basic " + decodedString;
		return basicAuth;
	}
	
	@Test
    public void createUserRoundTrip() throws Exception {
        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = WS.newClient(3333);
                Date now = new Date();
                UserDTO user1 = generateUser(String.valueOf(now.getTime()));
                
                // Registration
                CompletionStage<WSResponse> completionStage = ws.url("/rest/api/1/users/").post(Json.toJson(user1));
                WSResponse response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());
                
                // Authenticate
                String authentication = generateAuth(user1.getNickname(), user1.getPassword());
                completionStage = ws.url("/rest/api/1/users/login/").setHeader("authorization", authentication).get();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());
                assertThat(response.getBody().length(), equalTo(16));
                String token = response.getBody();
                
                // Get Profile
                completionStage = ws.url("/rest/api/1/users/" + user1.getNickname()).setHeader("authorization", "Basic " + token).get();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());
                
                // Logout
                completionStage = ws.url("/rest/api/1/users/logout/").setHeader("authorization", "Basic " + token).get();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());

                // Check Fobidden
                completionStage = ws.url("/rest/api/1/users/" + user1.getNickname()).setHeader("authorization", "Basic " + token).get();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.FORBIDDEN, response.getStatus());
                
                // Login 2nd time
                completionStage = ws.url("/rest/api/1/users/login/").setHeader("authorization", authentication).get();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());
                assertThat(response.getBody().length(), equalTo(16));
                token = response.getBody();
                
                // Delete User
                completionStage = ws.url("/rest/api/1/users/" + user1.getNickname()).setHeader("authorization", "Basic " + token).delete();
                response = completionStage.toCompletableFuture().get();
                assertEquals(Status.OK, response.getStatus());
                
                ws.close();
                
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
            }
        });
    }
}
