package de.x132.prioritisation.controller;

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
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.UnauthorizedException;
import de.x132.common.exceptionhandling.UnknownSolverException;
import de.x132.prioritisation.service.PrioritisationService;
import de.x132.prioritisation.transfer.PrioritisationDTO;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;

/**
 * Controller für den Zugriuff auf Objekte vom Typ Prioritisation
 * @author Max Wick
 *
 */
public class PrioritisationController extends Controller {

	private UserService userservice;
	private PrioritisationService prioritisationService;

	@Inject
	public PrioritisationController(UserService userservice, PrioritisationService prioritisationService) {
		this.userservice = userservice;
		this.prioritisationService = prioritisationService;
	}

	@Logging
	@Authenticated
	@Transactional
	public Result list(String projectname, int page, int size) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			List<PrioritisationDTO> prioritisations;
			try {
				prioritisations = prioritisationService.list(client, projectname, page, size);
			} catch (InternalServerException e) {
				return internalServerError(e.getMessage());
			}
			if (prioritisations.size() > 0) {
				return ok(Json.toJson(prioritisations));
			} else {
				return noContent();
			}
		}
		return ok();
	}

	@Logging
	@Authenticated
	@Transactional
	public Result create(String projectname) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			PrioritisationDTO prioritisation = Json.fromJson(json, PrioritisationDTO.class);
			try {
				Optional<PrioritisationDTO> retOpt = prioritisationService.create(client, projectname, prioritisation);
				if (retOpt.isPresent()) {
					return ok(Json.toJson(retOpt.get()));
				} else {
					return noContent();
				}
			} catch (NotFoundException e) {
				return notFound(e.getMessage());
			} catch (UnknownSolverException e) {
				return badRequest(e.getMessage());
			}
		}
		return notFound("Benutzer konnte nicht gefunden werden.");
	}

	@Authenticated
	@Transactional
	public Result get(String projectname, String prioritisation) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			Optional<PrioritisationDTO> retOpt;
			try {
				retOpt = prioritisationService.get(client, projectname, prioritisation);
			} catch (NotFoundException e) {
				return notFound(e.getMessage());
			}
			if (retOpt.isPresent()) {
				return ok(Json.toJson(retOpt.get()));
			} else {
				return noContent();
			}
		}
		return ok();
	}

	@Logging
	@Authenticated
	@Transactional
	public Result delete(String projectname, String prioritisation) {
		try {
			Client client = userservice.getClient(request()).orElseThrow(
					() -> new UnauthorizedException("Not Authrorized"));

			Optional<PrioritisationDTO> retOpt = prioritisationService.delete(client, projectname, prioritisation);
			if (retOpt.isPresent()) {
				return ok(Json.toJson(retOpt.get()));
			} else {
				return noContent();
			}
		} catch (NotFoundException e) {
			return notFound(e.getMessage());
		} catch (UnauthorizedException e) {
			return unauthorized();
		}
	}

	@Logging
	@Authenticated
	@Transactional
	public Result update(String projectname, String prioritisation) {
		Optional<Client> optClient = userservice.getClient(request());
		if (optClient.isPresent()) {
			Client client = optClient.get();
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			PrioritisationDTO prioritisationDto = Json.fromJson(json, PrioritisationDTO.class);
			Optional<PrioritisationDTO> retOpt;
			try {
				retOpt = prioritisationService.update(client, projectname, prioritisationDto);
			} catch (NotFoundException e) {
				return notFound("Es konnte keine Priorisierung gefunden werden.");
			}
			if (retOpt.isPresent()) {
				return ok(Json.toJson(retOpt.get()));
			} else {
				return noContent();
			}
		}
		return ok();
	}
}
