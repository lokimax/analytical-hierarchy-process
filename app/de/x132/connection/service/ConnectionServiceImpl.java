package de.x132.connection.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.JPAApi;
import de.x132.common.AbstractService;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.connection.models.Connection;
import de.x132.connection.transfer.ConnectionDTO;
import de.x132.node.models.Node;
import de.x132.node.service.NodeService;
import de.x132.project.models.Project;
import de.x132.project.service.ProjectService;
import de.x132.user.models.Client;

/**
 * Implementierung des Services {@link ConnectionService} um auf die Verbindung
 * zwischen zwei Knoten zu zu greifen.
 * @author Max Wick
 *
 */
public class ConnectionServiceImpl extends AbstractService<ConnectionDTO, Connection> implements ConnectionService {

	private JPAApi jpa;
	private ProjectService projectService;
	private NodeService nodeservice;

	@Inject
	public ConnectionServiceImpl(JPAApi jpa, ProjectService projectService, NodeService nodeservice) {
		super(ConnectionDTO.class);
		this.jpa = jpa;
		this.projectService = projectService;
		this.nodeservice = nodeservice;
	}

	/**
	 * @see ConnectionService#getConnections(Client, String, int, int)
	 */
	@Override
	public List<ConnectionDTO> getConnections(Client client, String projectname, int page, int size)
			throws InternalServerException {
		Optional<Project> optionalProject = projectService.getProject(client.getNickname(), projectname);
		if (optionalProject.isPresent()) {
			Project project = optionalProject.get();
			TypedQuery<Connection> query = jpa.em().createNamedQuery(Connection.FIND_ALL_CONNECTION_FOR_PROJECT,
					Connection.class);
			query.setParameter("nickname", project.getClient().getNickname());
			query.setParameter("projectname", project.getName());
			query.setFirstResult((page - 1) * size);
			query.setMaxResults(size);

			List<Connection> connections = (List<Connection>) query.getResultList();
			List<ConnectionDTO> connectionDtos;
			try {
				connectionDtos = mapList(connections);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new InternalServerException(e);
			}
			return connectionDtos;
		}
		return Collections.emptyList();
	}
	
	@Override
	public List<ConnectionDTO> getConnections(Client client, String projectname, String startnode)
			throws InternalServerException {
		Optional<Project> optionalProject = projectService.getProject(client.getNickname(), projectname);
		if (optionalProject.isPresent()) {
			Project project = optionalProject.get();
			TypedQuery<Connection> query = jpa.em().createNamedQuery(Connection.FIND_ALL_CONNECTION_FOR_STARTNODE,
					Connection.class);
			query.setParameter("nickname", project.getClient().getNickname());
			query.setParameter("projectname", project.getName());
			query.setParameter("sourcenode", startnode);

			List<Connection> connections = (List<Connection>) query.getResultList();
			List<ConnectionDTO> connectionDtos;
			try {
				connectionDtos = mapList(connections);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new InternalServerException(e);
			}
			return connectionDtos;
		}
		return Collections.emptyList();
	}

	/**
	 * @see ConnectionService#create(Client, String, ConnectionDTO)
	 */
	@Override
	public Optional<ConnectionDTO> create(Client client, String projectName, ConnectionDTO connectionDto) {
		Optional<Project> optProject = this.projectService.getProject(client.getNickname(), projectName);
		if (optProject.isPresent()) {
			Project project = optProject.get();

			Optional<Node> optSourceNode = this.nodeservice.get(project, connectionDto.getSourcenode());
			Optional<Node> optTargetNode = this.nodeservice.get(project, connectionDto.getTargetnode());

			if (optSourceNode.isPresent() && optTargetNode.isPresent()) {
				Connection connection = new Connection();
				connection.setSourcenode(optSourceNode.get());
				connection.setTargetnode(optTargetNode.get());
				connection.setProject(project);
				connection.setModifiedBy(project.getClient());

				jpa.em().persist(connection);

				mapToDto(connection, connectionDto);
				return Optional.of(connectionDto);
			}
			return Optional.empty();
		}

		return Optional.empty();
	}

	/**
	 * @see ConnectionService#getConnection(Client, String, String, String)
	 */
	@Override
	public Optional<ConnectionDTO> getConnection(Client client, String projectname, String startNode, String finishNode) {
		Optional<Connection> optConnection = this.get(client, projectname, startNode, finishNode);
		if (optConnection.isPresent()) {
			ConnectionDTO retDto = new ConnectionDTO();
			mapToDto(optConnection.get(), retDto);
			return Optional.of(retDto);
		}
		return Optional.empty();
	}

	/**
	 * Funktion zu Selektion von Verbindungen. * @param client für welchen die
	 * Verbindung gelesen werden soll.
	 * @param projectname Name des Projektes, in dem die Verbindung liegt.
	 * @param startNode Elternknoten der Verbdindung
	 * @param targetNode Kindknoten der Verbindung.
	 * @return Das Entity der Verbindung oder Optional.absent()
	 */
	private Optional<Connection> get(Client client, String projectname, String startNode, String targetNode) {
		Query query = jpa.em().createNamedQuery(Connection.FIND_CONNECTION);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("projectname", projectname);
		query.setParameter("sourcenode", startNode);
		query.setParameter("targetnode", targetNode);
		Connection connection = (Connection) query.getSingleResult();
		return Optional.of(connection);
	}

	/**
	 * @see ConnectionService#delete(Client, String, String, String)
	 */
	@Override
	public Optional<ConnectionDTO> delete(Client client, String projectname, String startNode, String finishNode) {
		Optional<Connection> optConnection = this.get(client, projectname, startNode, finishNode);
		if (optConnection.isPresent()) {
			Connection connection = optConnection.get();
			ConnectionDTO retDTO = new ConnectionDTO();
			mapToDto(connection, retDTO);
			jpa.em().remove(connection);
			return Optional.of(retDTO);
		}
		return Optional.empty();
	}

	/**
	 * Wird für Verbindungen nicht verwendet.
	 */
	@Override
	public void mapToEntity(ConnectionDTO source, Connection target) {
		// No needed for this Context
	}

	/**
	 * @see AbstractService#mapToDto(de.x132.common.CommonInformation, de.x132.common.AbstractDTO)
	 */
	@Override
	public void mapToDto(Connection source, ConnectionDTO target) {
		target.setSourcenode(source.getSourcenode().getName());
		target.setTargetnode(source.getTargetnode().getName());
	}
}
