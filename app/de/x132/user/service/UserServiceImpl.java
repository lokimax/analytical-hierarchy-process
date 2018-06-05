package de.x132.user.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.codec.binary.Hex;

import de.x132.common.AbstractService;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.user.models.Client;
import de.x132.user.models.UserStatus;
import de.x132.user.transfer.UserDTO;
import play.Configuration;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Http.Request;


public class UserServiceImpl extends AbstractService<UserDTO,Client>  implements UserService {

	private JPAApi jpaApi;

	private MailerClient mailer;

	private Configuration config;

	@Inject
	public UserServiceImpl(JPAApi api, MailerClient mailer, Configuration config) {
		super(UserDTO.class);
		this.jpaApi = api;
		this.mailer = mailer;
		this.config = config;
	}

	public Optional<UserDTO> delete(String nickname) throws NotFoundException {
		Logger.debug("Delete " + nickname);
		Client client = getClient(nickname).orElseThrow(() -> new NotFoundException("Client cannot be found."));

		JPA.em().remove(client);
		UserDTO userDto = new UserDTO();
		mapToDto(client, userDto);
		return Optional.of(userDto);
	}

	public void activate(String activationcode) {
		Logger.debug("Activate for code  {0}", activationcode);

		Query query = jpaApi.em().createNamedQuery(Client.FIND_BY_ACTIVATION);
		query.setParameter("activationCode", activationcode);

		Client client = (Client) query.getSingleResult();
		client.setStatus(UserStatus.ACTIVE);
		client.setModifiedBy(client);
		jpaApi.em().persist(client);
	}

	
	public Optional<UserDTO> getUser(String nickname) {
		Logger.debug("Get User " + nickname);
		Query query = jpaApi.em().createNamedQuery(Client.FIND_BY_NICKNAME);
		query.setParameter("nickname", nickname);
		try {
			Client client = (Client) query.getSingleResult();
			UserDTO returnDto = new UserDTO();
			mapToDto(client, returnDto);
			return Optional.ofNullable(returnDto);
		} catch (NoResultException nre) {
			return Optional.empty();
		}
	}

	public Optional<UserDTO> create(UserDTO user) {
		Logger.debug("create user " + user.getName());
		Client client = new Client();
		mapToEntity(user, client);
		Boolean withEmailActivation = this.config.getBoolean("email.activation.on");
		Logger.info("withEmailActivation" + withEmailActivation.toString());
		
		if (Boolean.TRUE.equals(withEmailActivation)) {
			client.setStatus(UserStatus.REGISTRED);
			sendActivationCode(client);
		} else {
			client.setStatus(UserStatus.ACTIVE);
		}
		client.setModifiedBy(client);
		jpaApi.em().persist(client);
		UserDTO returnDto = new UserDTO();
		mapToDto(client, returnDto);
		return Optional.of(user);
	}

	private void sendActivationCode(Client client) {
		String activationCode = generateRandomString(32);
		client.setActivationCode(activationCode);

		final Email email = new Email()
				.setSubject("Activation Email")
				.setFrom("AHP Connect <max@x132.de>")
				.addTo(client.getName() + " " + client.getSurename() + " <" + client.getEmail() + ">")
				.setBodyText(
						"Your Activation Code: " + this.config.getString("email.activation.baseUrl")
								+ activationCode);
		mailer.send(email);
	}

	@Override
	public Optional<UserDTO> getUser(Request request) {
		Optional<Client> client = getClient(request);
		if (client.isPresent()) {
			UserDTO returnDto = new UserDTO();
			mapToDto(client.get(), returnDto);
			return Optional.of(returnDto);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Client> getClient(Request request) {
		String header = request.getHeader("authorization");
		String strToken = header.substring(6, header.length()).trim();
		try {
			Logger.debug("StrToken: " + strToken);
			Query query = jpaApi.em().createNamedQuery(Client.FIND_BY_TOKEN);
			query.setParameter("token", strToken);
			Client client = (Client) query.getSingleResult();
			return Optional.ofNullable(client);
		} catch(NoResultException nre){
			return Optional.empty();
		}
	}

	@Override
	public List<UserDTO> getUsers(int page, int count) {
		List<Client> clients = getUsersInternal(page, count);
		List<UserDTO> userDtos = new ArrayList<>();
		map(clients, userDtos);
		return userDtos;
	}

	@Override
	public Optional<UserDTO> update(UserDTO user) throws NotFoundException {
		Client client = getClient(user.getNickname()).orElseThrow(() -> new NotFoundException("Client cannot be found."));
		mapToEntity(user, client);
		client.setModifiedBy(client);
		this.jpaApi.em().persist(client);
		mapToDto(client, user);
		return Optional.of(user);
	}

	private void map(List<Client> source, List<UserDTO> target) {
		for (Client client : source) {
			UserDTO userdto = new UserDTO();
			mapToDto(client, userdto);
			target.add(userdto);
		}
	}

	private Optional<Client> getClient(String nickname) {
		Query query = jpaApi.em().createNamedQuery(Client.FIND_BY_NICKNAME);
		query.setParameter("nickname", nickname);
		Client client = (Client) query.getSingleResult();
		return Optional.of(client);
	}

	private List<Client> getUsersInternal(int page, int count) {
		TypedQuery<Client> query = jpaApi.em().createNamedQuery(Client.FIND_LIST, Client.class);
		query.setFirstResult((page - 1) * count);
		query.setMaxResults(count);
		List<Client> clients = query.getResultList();
		return clients;
	}

	private static String generateRandomString(int size) {
		SecureRandom random = new SecureRandom();
		byte secure[] = new byte[size];
		random.nextBytes(secure);
		String activationCode = Hex.encodeHexString(secure);
		return activationCode;
	}

	@Override
	public void mapToEntity(UserDTO source, Client target) {
		target.setName(source.getName());
		target.setSurename(source.getSurename());
		target.setPassword(source.getPassword());
		target.setHostId(source.getHostId());
		target.setNickname(source.getNickname());
		target.setEmail(source.getEmail());
		
	}

	@Override
	public void mapToDto(Client source, UserDTO target) {
		target.setName(source.getName());
		target.setSurename(source.getSurename());
		// target.setPassword(source.getPassword());
		target.setHostId(source.getHostId());
		target.setNickname(source.getNickname());
		target.setEmail(source.getEmail());
	}

}
