package de.x132.test.utils;

import static play.test.Helpers.fakeApplication;

import java.util.HashMap;
import java.util.Map;

import de.x132.user.transfer.UserDTO;
import play.test.Helpers;
import play.test.TestServer;
import play.test.WithApplication;

public abstract class AbstractWiredTest extends WithApplication{
	
	private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

	int timeout = 500000;

	protected TestServer testServer(int port) {
		getLogger().info("creating Tests server");
		Map<String, String> config = new HashMap<String, String>();
		config.put("email.activation.on", "false");
		return Helpers.testServer(port, fakeApplication(config));
	}
	

	public UserDTO generateUser(String nickname) {
		UserDTO user1 = new UserDTO();
		user1.setEmail("max.wick@x132.de");
		user1.setNickname(nickname);
		user1.setName("Max");
		user1.setSurename("Test");
		user1.setPassword("test");
		user1.setHostId("http://localhost:9000");
		return user1;
	}

	public org.slf4j.Logger getLogger() {
		return logger;
	}

	public void setLogger(org.slf4j.Logger logger) {
		this.logger = logger;
	}

}
