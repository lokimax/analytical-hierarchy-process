package de.x132.comparison.controller;

import java.util.Optional;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.logging.Logging;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.comparison.service.ComparisonService;
import de.x132.comparison.transfer.ComparisonDTO;
import de.x132.user.actions.Authenticated;
import de.x132.user.models.Client;
import de.x132.user.service.UserService;

/**
 * Controller um Vergleiche zu speichern.
 * 
 * @author Max Wick
 *
 */
public class ComparisonController extends Controller {

	/**
	 * Service um auf Vergleiche zuzugreifen.
	 */
	private ComparisonService service;
	private UserService userservice;

	/**
	 * Konstruktor der injeziert wird von GUICE mit dem Service und der
	 * Userservice.
	 * @param service
	 * @param userservice
	 */
	@Inject
	public ComparisonController(ComparisonService service, UserService userservice) {
		this.service = service;
		this.userservice = userservice;
	}

	/**
	 * Liefert einen bestimmten Vergleich.
	 * @param projectname in dem die PRiorisierung liegt.
	 * @param prioritisation in dem der Vergleich definiert ist.
	 * @param parent Elternknoten des Vergleiches.
	 * @param leftnode Linker knoten für den Vergleich.
	 * @param rightnode Rechter Knoten für den Vergleich.
	 * @return Result mit dem Vergleich oder 404 wenn der Vergleich nicht
	 *         gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result get(String projectname, String prioritisation, String parent, String leftnode, String rightnode) {
		Optional<Client> client = userservice.getClient(request());
		if (client.isPresent()) {
			try {
				Optional<ComparisonDTO> comparison = service.get(client.get(), projectname, prioritisation, parent,
						leftnode, rightnode);
				if (comparison.isPresent()) {
					return ok(Json.toJson(comparison.get()));
				}
			} catch (NotFoundException e) {
				return notFound(e.getMessage());
			}
		}
		return notFound();
	}

	/**
	 * Updatet einen Vergleich in der Datenbank.
	 * @param projectname in dem die Priorisierung liegt.
	 * @param prioritisation in der der Vergleich liegt.
	 * @return der upgedatetet Vergleich.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result update(String projectname, String prioritisation) {
		Optional<Client> client = userservice.getClient(request());
		if (client.isPresent()) {
			JsonNode json = request().body().asJson();
			ComparisonDTO comparison = Json.fromJson(json, ComparisonDTO.class);
			Optional<ComparisonDTO> optComparison;
			try {
				optComparison = service.update(client.get(), projectname, prioritisation, comparison);
				if (optComparison.isPresent()) {
					return ok(Json.toJson(optComparison.get()));
				}
			} catch (NotFoundException e) {
				return notFound(e.getMessage());
			}
		}
		return notFound();
	}

}
