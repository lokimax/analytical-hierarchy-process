package de.x132.project.controller;

import static de.x132.common.ErrorResponseUtils.createError;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.logging.Logging;

import com.fasterxml.jackson.databind.JsonNode;

import de.x132.common.ErrorType;
import de.x132.project.service.ProjectService;
import de.x132.project.transfer.ProjectDTO;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;

/**
 * Klasse für den Project Controller, der über dne Router aufgerufen wird.
 * 
 * @author Max Wick
 * */
public class ProjectController extends Controller {

	private ProjectService projectservice;
	private UserService userservice;

	/**
	 * Innerhalb des Controllers wird auf den Benutzer und auf die Projekte
	 * selbst zugegriffen.
	 * 
	 * @param userservice
	 *            um dne Benutzer zu verifizieren.
	 * @param projectservice
	 *            um Projekte mit der Datenbank zu kommunizieren.
	 */
	@Inject
	public ProjectController(UserService userservice, ProjectService projectservice) {
		this.userservice = userservice;
		this.projectservice = projectservice;
	}

	/**
	 * Liefert eine Liste von Projekten.
	 * 
	 * @param page
	 *            welche Seitre soll geleiefert werden.
	 * @param size
	 *            wieviele Elemente soll die Seite beinhalten.
	 * @return EinResult mit einer Liste von DTOs über die Projekte. oder
	 *         HTTP-Code 404 wenn der Benutzer nicht gefunden werden konnte,
	 *         oder HTTP-CODE 204 wenn keiene Projekte vorhanden.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result list(int page, int size) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			List<ProjectDTO> projects = this.projectservice.getProjects(client);
			if (projects.size() > 0) {
				return ok(Json.toJson(projects));
			} else {
				return noContent();
			}
		}
		return notFound(createError(ErrorType.WARNING, "Benutzer konnte nicht gefunden werden!"));
	}

	/**
	 * Erstellt ein neues Projekt.
	 * 
	 * @return Ein Result über den Erfolg des Erstellens oder HTTP COde 503 wenn
	 *         die übergebenen Parameter invalide sind, oder HTTP-404 Not Found
	 *         wenn der Benutzer nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result create() {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			Client client = optClient.get();
			ProjectDTO project = Json.fromJson(json, ProjectDTO.class);
			Optional<ProjectDTO> optProjectDto = projectservice.create(client, project);

			if (optProjectDto.isPresent()) {
				return ok(Json.toJson(optProjectDto.get()));
			} else {
				return badRequest(createError(ErrorType.WARNING, "Projekt konnte nicht erstellt werdens"));
			}
		}
		return notFound(createError(ErrorType.WARNING, "Benutzer konnte nicht gefunden werden!"));
	}

	/**
	 * Liefert einen bestimmtes Projekt für den angemeldeten Benutzer zurück.
	 * 
	 * @param projectname
	 *            Name des Projektes.
	 * @return Result mit dem Projekt, oder HTTP 404 wenn der Benutzer oder das
	 *         Projekt nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result get(String projectname) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			Optional<ProjectDTO> optProjectDto = projectservice.get(client, projectname);
			if (optProjectDto.isPresent()) {
				return ok(Json.toJson(optProjectDto.get()));
			} else {
				return notFound();
			}
		}
		return notFound(createError(ErrorType.WARNING, "Benutzer konnte nicht gefunden werden!"));
	}

	/**
	 * Updatet ein Projekt innerhalb der Datenbank.
	 * 
	 * @param projectname
	 *            Name des Projektes, welches upgedatet werden soll.
	 * @return Das upgedatete Projekt, oder HTTP Code 503 wenn das übergeben
	 *         Body invalid war. Oder HTTP Code 404 wenn der Benutzer nicht
	 *         gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result update(String projectname) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest(createError(ErrorType.WARNING, "Das gesendete Objekt konnte nicht übersetzt werden."));
			}
			Client client = optClient.get();
			ProjectDTO project = Json.fromJson(json, ProjectDTO.class);
			Optional<ProjectDTO> optProjectDto = projectservice.update(client, projectname, project);

			if (optProjectDto.isPresent()) {
				return ok(Json.toJson(optProjectDto.get()));
			} else {
				return badRequest(createError(ErrorType.WARNING, "Projekt konnte nicht gefunden werden!"));
			}
		}
		return notFound(createError(ErrorType.WARNING, "Benutzer konnte nicht gefunden werden!"));
	}

	/**
	 * Löscht ein Projekt.
	 * 
	 * @param projectname
	 *            welches gelöscht werden sollte.
	 * @return Result mit dem gelöschten Objekt, HTTP Code 404 wenn das Projekt
	 *         nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result delete(String projectname) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			Optional<ProjectDTO> optProjectDto = projectservice.delete(client, projectname);
			if (optProjectDto.isPresent()) {
				return ok(Json.toJson(optProjectDto.get()));
			} else {
				return notFound(createError(ErrorType.WARNING, "Projekt konnte nicht gefunden werden!"));
			}
		}
		return notFound(createError(ErrorType.WARNING, "Benutzer konnte nicht gefunden werden!"));
	}

}
