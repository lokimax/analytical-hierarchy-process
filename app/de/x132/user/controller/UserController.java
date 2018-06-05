package de.x132.user.controller;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.logging.Logging;

import com.fasterxml.jackson.databind.JsonNode;

import de.x132.common.exceptionhandling.BadRequestException;
import de.x132.common.exceptionhandling.ForbiddenException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.user.actions.Authenticated;
import de.x132.user.service.TokenService;
import de.x132.user.service.UserService;
import de.x132.user.transfer.UserDTO;

/**
 * Controller als REST Enpoint für den Benutzer. Über diesen Controller kann
 * sich der Benutzer regestrieren, anmelden, abmelden und den Benutzer account
 * löschen.
 * 
 * @author Max Wick
 *
 */
public class UserController extends Controller {

	/**
	 * Für den Zugriff auf den Benutzer aus der Datenbank
	 */
	private final UserService userservice;

	/**
	 * Für den Zugriff auf die Tokens der Benutzer.
	 */
	private TokenService tokenService;

	/**
	 * Konstruktor, der beim Hochfahren der Komponente injeziert wird.
	 * 
	 * @param tokenService
	 *            der Service zu den Tokens.
	 * @param userservice
	 *            der Service um den Benutzer zu handeln.
	 */
	@Inject
	public UserController(TokenService tokenService, UserService userservice) {
		this.tokenService = tokenService;
		this.userservice = userservice;
	}

	/**
	 * Erstellt einen Benutzeraccount und sendet diesen als Result in Form von
	 * JSON an den Aufrufer zurück.
	 * 
	 * @return HTTP 200 mit UserDTO als JSON Format im body der Nachricht, die
	 *         an den Benutzer zurück geschickt wird. HTTP 400 wenn der Benutzer
	 *         nicht erfolgreich angelegt werden konnte.
	 */
	@Logging
	@Transactional
	public Result create() {
		try {
			JsonNode json = request().body().asJson();
			if (json == null) {
				return badRequest();
			}
			UserDTO user = Json.fromJson(json, UserDTO.class);
			UserDTO createdUser = userservice.create(user)
					.orElseThrow(() -> new BadRequestException("Benutzer konnte nicht erfolgreich erstellt werden."));
			return ok(Json.toJson(createdUser));
		} catch (BadRequestException ex) {
			return badRequest(ex.getMessage());
		} 
	}

	/**
	 * Aktiviert einen Benutzer anhand eines einmal Tokens.
	 * 
	 * @param activationcode
	 *            einmalToken der als Email versendet wird.
	 * @return HTTP 200 wenn der Benutzer erfolgreich aktiviert werden konnte,
	 *         ansonsten HTTP 400.
	 */
	@Logging
	@Transactional
	public Result activate(String activationcode) {
		try {
			userservice.activate(activationcode);
			return ok();
		} catch (Exception ex) {
			return badRequest("Aktivierung fehlgeschlagen");
		}
	}

	/**
	 * Löscht den Benutzer, der das nickname <code>nickname</code> hat.
	 * 
	 * @param nickname
	 *            des zu löschenden Benutzers.
	 * @return HTTP 200 wenn der Benutzer erfolgreich gelöscht werden konnte.
	 *         <br>
	 *         HTTP 404 wenn der Benutzer nicht gefunden werden konnte.
	 */
	@Logging
	@Transactional
	public Result delete(String nickname) {
		Optional<UserDTO> userDto;
		try {
			userDto = userservice.delete(nickname);
			if (userDto.isPresent()) {
				return ok(Json.toJson(userDto.get()));
			} else {
				return notFound();
			}
		} catch (NotFoundException e) {
			return notFound();
		}
	}

	/**
	 * Liefert einen Benutzer im JSON Format.
	 * 
	 * @param nickname
	 *            welcher als JSON Format geliefert werden soll.
	 * @return HTTP 200 mit Benutzer als JSON Format im Body der Nachricht. <br>
	 *         HTTP 404 wenn der Benutzer nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result get(String nickname) {
		Optional<UserDTO> user = userservice.getUser(nickname);
		if (user.isPresent()) {
			return ok(Json.toJson(user.get()));
		}
		return notFound();
	}

	/**
	 * Liefert eine Liste der Regestrierten Benutzer.
	 * 
	 * @param page
	 *            welche Seite der Liste soll geliefert werden, Seite ist
	 *            abhängig von Anzahl der darzustellenden Benutzer.
	 * @param count
	 *            Anzahl der darzustellenden Benutzer pro Seite.
	 * @return HTTP 200 mit der angeforderten Liste der Benutzer.
	 */
	@Logging
	@Transactional
	public Result list(int page, int count) {
		List<UserDTO> users = this.userservice.getUsers(page, count);
		return ok(Json.toJson(users));
	}

	/**
	 * Meldet einen Benutzer im System an, es wird ein Token geliefert, der im
	 * Header des nächsten calls mitgegeben werden soll.
	 * 
	 * @return HTTP 200 mit dem Token im Body, der im Header des nächsten Call
	 *         vom Client mitgeliefert werden soll. <br>
	 *         HTTP 400 wenn der Token nicht berechnet werden konnte. HTTP 404
	 *         wenn der Benutzer nicht gefunden werden konnte.
	 */
	@Transactional
	public Result login() {
		try {
			Logger.debug("Login");
			String token = tokenService.getToken(request()).orElseThrow(() -> new BadRequestException("Invalid Token"));
			return ok(token);
		} catch (NotFoundException e) {
			return notFound();
		} catch (ForbiddenException e) {
			return forbidden();
		} catch (BadRequestException e) {
			return badRequest(e.getMessage());
		}
	}

	/**
	 * Loggt einen Benutzer aus dem System aus, es werden alle Tokens zum
	 * Benutzer gelöscht.
	 * 
	 * @return HTTP 200 wenn das Löschen erfolgreich war. <br>
	 *         HTTP 404 wenn der Benutzer nicht gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result logout() {
		try {
			this.tokenService.deleteTokens(request());
			return ok();
		} catch (NotFoundException e) {
			return notFound();
		}
	}

	/**
	 * Updatet einen Benutzer, anhand des JSONs der im Body der NAchricht
	 * gesendet wird.
	 * 
	 * @return HTTP 200 wenn die Aktualiserung durchgeführt werden konnte.<br>
	 *         HTTP 400 wenn JSON ungültig war. <br>
	 * 		   HTTP 404 wenn der Benutzer, der aktualisiert werden konnte nicht
	 *         gefunden werden konnte.
	 */
	@Logging
	@Authenticated
	@Transactional
	public Result update() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest();
		}
		UserDTO user = Json.fromJson(json, UserDTO.class);
		Optional<UserDTO> userDto;
		try {
			userDto = userservice.update(user);
		} catch (NotFoundException e) {
			return notFound();
		}
		if (userDto.isPresent()) {
			return ok(Json.toJson(userDto.get()));
		}
		return ok();
	}

}
