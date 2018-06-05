package de.x132.connection.controller;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.logging.Logging;

import com.fasterxml.jackson.databind.JsonNode;

import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.connection.service.ConnectionService;
import de.x132.connection.transfer.ConnectionDTO;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;

/**
 * Controller um eine Connection zu erstellen.
 * 
 * @author Max Wick
 *
 */
public class ConnectionController extends Controller {

	private UserService userservice;
	private ConnectionService connectionService;

	/**
	 * Injezierung der Service
	 * @param userservice
	 * @param connectionService
	 */
	@Inject
	public ConnectionController(UserService userservice, ConnectionService connectionService) {
		this.userservice = userservice;
		this.connectionService = connectionService;
	}

	/**
	 * Liefert eine Liste von Verbindungen um diese auf einer Seite
	 * darzustellen.
	 * @param projectname aus dem die Verbindungen dargestellt werden sollen.
	 * @param page seite auf der die Verbindungen sich befinden.
	 * @param size Anzahl der Elemente.
	 * @return Result mit den Verbindungen oder 500 internal server error
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result list(String projectname, int page, int size) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			List<ConnectionDTO> connections;

			try {
				connections = this.connectionService.getConnections(client, projectname, page, size);
			} catch (InternalServerException e) {
				return internalServerError(e.getMessage());
			}

			if (connections.size() > 0) {
				return ok(Json.toJson(connections));
			} else {
				return noContent();
			}
		}
		return notFound();
	}

	/**
	 * Erstellt eine neue Verbindung zwischen zwei Knoten.
	 * @param projectName Name des Projektes, in dem die Verbidnung hergestellt
	 *            werden soll.
	 * @return Result mit der erstellten Verbindung, oder HTTP Code 404 wenn ein
	 *         Fehler passiert ist. Oder 400 Badr Request.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result create(String projectName) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			Client client = optClient.get();
			ConnectionDTO connection = Json.fromJson(json, ConnectionDTO.class);

			Optional<ConnectionDTO> optConnection = connectionService.create(client, projectName, connection);

			if (optConnection.isPresent()) {
				return ok(Json.toJson(optConnection.get()));
			} else {
				return badRequest();
			}
		}
		return notFound();
	}

	/**
	 * Liefert eine bestehende Verbindung zurück.
	 * @param projectname Name des Projektes, indem die Verbindung liegt.
	 * @param startNode von dem die Verbindung ausgeht (Elternknoten).
	 * @param finishNode zu dem die Verbindung geht (Kindknoten).
	 * @return ein Result mit der Verbindung, oder HTTP CODE 404 wenn die
	 *         Verbindung nicht gefunden werden konnte oder der Benutzer nicht
	 *         eindeutig gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result get(String projectname, String startNode, String finishNode) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			Optional<ConnectionDTO> optConnectionDto = connectionService.getConnection(client, projectname, startNode,
					finishNode);
			if (optConnectionDto.isPresent()) {
				return ok(Json.toJson(optConnectionDto.get()));
			} else {
				return notFound();
			}
		}
		return notFound();
	}
	
	/**
	 * Liefert eine Liste von Verbindungen zurück, die von dem jeweiligen Knoten ausgehen.
	 * @param projectname Name des Projektes, indem die Verbindung liegt.
	 * @param startNode von dem die Verbindung ausgeht (Elternknoten).
	 * @return ein Result mit der Verbindung, oder HTTP CODE 404 wenn die
	 *         Verbindung nicht gefunden werden konnte oder der Benutzer nicht
	 *         eindeutig gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result getForStartnode(String projectname, String startNode) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			List<ConnectionDTO> connections;

			try {
				connections = this.connectionService.getConnections(client, projectname, startNode);
			} catch (InternalServerException e) {
				return internalServerError(e.getMessage());
			}

			if (connections.size() > 0) {
				return ok(Json.toJson(connections));
			} else {
				return noContent();
			}
		}
		return notFound();
	}

	/**
	 * Löscht eine Verbindung innerhalb eines Projektes.
	 * @param projectname Name des Projektes in dem die Verbindung gelöscht
	 *            werden soll.
	 * @param startNode Elternknoten aus dem die Verbindugn ausgeht.
	 * @param finishNode Kindknoten zu dem die Verbindung geht.
	 * @return ein Result mit der gelöschten Verbindung, oder mit HTTP CODE 404
	 *         wenn die VErbindung nicht gefunden werden konnte.
	 */
	@Authenticated
	@Transactional
	public Result delete(String projectname, String startNode, String finishNode) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			Optional<ConnectionDTO> optConnectionDto = connectionService.delete(client, projectname, startNode,
					finishNode);
			if (optConnectionDto.isPresent()) {
				return ok(Json.toJson(optConnectionDto.get()));
			} else {
				return notFound();
			}
		}
		return notFound();
	}

}
