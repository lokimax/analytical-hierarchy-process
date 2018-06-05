package de.x132.user.service;

import java.util.Optional;

import de.x132.common.exceptionhandling.ForbiddenException;
import de.x132.common.exceptionhandling.NotFoundException;
import play.mvc.Http.Request;

/**
 * Service zum generieren von Basic Auth Tokens.
 * @author Max Wick
 *
 */
public interface TokenService {

	/**
	 * Überprüft ob innerhalb des HTTP Header der entsprechende Token vorhanden
	 * ist.
	 * @param request HTTP Request.
	 * @return ein Optional mit einem Boolean, TRUE der Benutzer darf passieren.
	 *         False der Benutzer darf nicht passieren.
	 */
	Optional<Boolean> isValid(Request request);

	/**
	 * Generiert für einen Benutzer einen Token, der Benutzer wird anhand des im HTTP Headers mitgeschickten authorization parameters identifiziert.
	 * Das Passwort und Benutzername sind hierbei base64 codiert.
	 * @param request der HTTP Header.
	 * @return ein Optional mit einem Token wenn der Benutzer im System aktiv ist.
	 * @throws NotFoundException Wenn der Benutzer nicht gefunden worden ist.
	 * @throws ForbiddenException Wenn der Benutzer nicht  den Status Aktiv hat.
	 */
	Optional<String> getToken(Request request) throws NotFoundException, ForbiddenException;

	/**
	 * Löscht alle tokens für den Benutzer, voraussgesetzt der Benutzer ist aktiv.
	 * @param request HTTP Request.
	 * @throws NotFoundException wenn der Benutzer nicht gefunden werden konnte.
	 */
	void deleteTokens(Request request) throws NotFoundException;
}
