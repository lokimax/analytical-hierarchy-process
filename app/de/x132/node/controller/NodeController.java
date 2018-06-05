package de.x132.node.controller;

import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import de.x132.common.exceptionhandling.BadRequestException;
import de.x132.common.exceptionhandling.ConflictException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.UnauthorizedException;
import de.x132.node.service.NodeService;
import de.x132.node.transfer.NodeDTO;
import de.x132.project.models.Project;
import de.x132.project.service.ProjectService;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.logging.Logging;

/**
 * NodeController zum Verwalten von Nodes.
 * 
 * @author Max Wick
 *
 */
public class NodeController extends Controller {

	private NodeService nodeService;
	private UserService userService;
	private ProjectService projectService;

	/**
	 * Standard Konstruktor der mit den einzelnen Servicen injeziert wird.
	 * 
	 * @param nodeService der eigentliche Service um auf die Businesslogik der
	 *            Nodeverwaltung zuzugreifen.
	 * @param userService Über den UserService wird der Zugang zu den
	 *            Clientprofilen erlaubt.
	 * @param projectService um auf das Projekt zu zugreifen in dem der Knoten
	 *            sich befindet.
	 */
	@Inject
	public NodeController(NodeService nodeService, UserService userService, ProjectService projectService) {
		this.nodeService = nodeService;
		this.userService = userService;
		this.projectService = projectService;
	}

	/**
	 * Liefert einen Knoten zurück.
	 * 
	 * @param projectname Name des Projektes in dem der Knoten liegt.
	 * @param nodename Name des Knoten der als DTO zurück geliefert werden soll.
	 * @return Result mit HTTP Status 200 OK mit einem {@link NodeDTO} , HTTP
	 *         Status 403 Unauthorized wenn der Benutzer nicht authentifiziert
	 *         ist. HTTP Status 404 Not Found, wenn der requestierte Knoten
	 *         nicht im System befindet.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result get(String projectname, String nodename) {
		try {
			Client client = userService.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));

			Project project = projectService.getProject(client.getNickname(), projectname).orElseThrow(
					() -> new NotFoundException("No Project with the name " + projectname + " found"));

			NodeDTO node = nodeService.getNode(project, nodename).orElseThrow(
					() -> new NotFoundException("No Node with the name " + nodename + " found"));

			return ok(Json.toJson(node));

		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (UnauthorizedException e) {
			return unauthorized();
		}
	}

	/**
	 * Liefert eine Liste von Knoten für eine Projekt zurück.
	 * @param projectname des Projektes.
	 * @param page Seite der Liste die dargestellt werden soll.
	 * @param count Anzahl der Elemente in der Liste-
	 * @return Ein Result mit einer Liste von Knoten.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result list(String projectname, int page, int count) {
		try {
			Client client = userService.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));

			Project project = projectService.getProject(client.getNickname(), projectname).orElseThrow(
					() -> new NotFoundException("No Project with the name " + projectname + " found"));

			List<NodeDTO> retList = this.nodeService.list(project, page, count);
			return ok(Json.toJson(retList));
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (UnauthorizedException e) {
			return unauthorized();
		} catch (InternalServerException e) {
			return internalServerError(e.getMessage());
		}
	}

	/**
	 * Erstellt einen neuen Knoten innerhalb eines Projektes. Der Knoten wird
	 * aus dem Body des HTTP Requests ausgelesen.
	 * @param projectName des Projektes, indem der Knoten erstellt werden soll.
	 * @return Ein Result mit dem erstellten Knoten oder einer Fehlermeldung.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result create(String projectName) {
		try {
			Client client = userService.getClient(request()).orElseThrow(() -> new NotFoundException("User not Found"));
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			NodeDTO node = Json.fromJson(json, NodeDTO.class);
			Project project = projectService.getProject(client.getNickname(), projectName).orElseThrow(
					() -> new NotFoundException("Project not Found"));
			NodeDTO nodeAfterSave = nodeService.create(project, node).orElseThrow(
					() -> new BadRequestException("Cannot create Node"));
			return ok(Json.toJson(nodeAfterSave));
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		} catch (ConflictException e) {
			return status(CONFLICT, e.getMessage());
		}
	}

	/**
	 * Editiert einen bestehenden Knoten.
	 * @param projectname in dem der Knoten liegt.
	 * @param nodename welches in der Datenbank erneuert werden soll (ein neuer
	 *            Name kann über DTO definiert werden)
	 * @return ein Response mit dem DTO oder HTTP Code 404 wenn das Produkt
	 *         nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result update(String projectname, String nodename) {
		try {
			Client client = userService.getClient(request()).orElseThrow(() -> new NotFoundException("User not Found"));

			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			NodeDTO node = Json.fromJson(json, NodeDTO.class);
			Project project = projectService.getProject(client.getNickname(), projectname).orElseThrow(
					() -> new NotFoundException("Project not Found"));

			NodeDTO nodeAfterSave = nodeService.update(project, nodename, node).orElseThrow(
					() -> new BadRequestException("Cannot create Node"));
			return ok(Json.toJson(nodeAfterSave));
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}
	
	/**
	 * Editiert einen bestehen Knoten.
	 * @param projectname in dem der Knoten liegt.
	 * @param nodename welches in der Datenbank erneuert werden soll (ein neuer
	 *            Name kann über DTO definiert werden)
	 * @return ein Response mit dem DTO oder HTTP Code 404 wenn das Produkt
	 *         nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result delete(String projectname, String nodename) {
		try {
			Client client = userService.getClient(request()).orElseThrow(() -> new NotFoundException("User not Found"));

			Project project = projectService.getProject(client.getNickname(), projectname).orElseThrow(
					() -> new NotFoundException("Project not Found"));

			NodeDTO nodeAfterSave = nodeService.delete(project, nodename).orElseThrow(
					() -> new BadRequestException("Cannot delete Node"));
			return ok(Json.toJson(nodeAfterSave));
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}	

}
