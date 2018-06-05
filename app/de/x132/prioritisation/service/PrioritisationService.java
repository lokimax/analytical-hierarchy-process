package de.x132.prioritisation.service;

import java.util.List;
import java.util.Optional;

import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.UnknownSolverException;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.prioritisation.transfer.PrioritisationDTO;
import de.x132.user.models.Client;

/**
 * Service um auf Priorisierungen innerhalb eines Projektes zuzugreifen.
 * @author Max Wick
 *
 */
public interface PrioritisationService {

	/**
	 * Liefert eine Liste von Priorisierungen innerhalb eines Projektes.
	 * @param client zu dem das Projekt gehört-
	 * @param projectname in dem die Priorisierungen liegen.
	 * @param page welche Seite soll dargestellt werden.
	 * @param size Anzahl der Elemente in der Liste.
	 * @return eine Liste von DTOs der Priorisierungen.
	 * @throws InternalServerException
	 */
	List<PrioritisationDTO> list(Client client, String projectname, int page, int size) throws InternalServerException;

	/**
	 * Erstellt eine Priorisierun innerhalb eines Projektes.
	 * @param client zu dem die Priorisierung erstellt werden soll.
	 * @param projectname Name des Projektes in dem die Priorisierung erstellt
	 *            werden soll.
	 * @param prioritisation Name der Priorisierung die innerhalb des Projektes
	 *            erstellt werden soll.
	 * @return Eine DTO der Priorisierung-
	 * @throws NotFoundException Wenn kein Projekt gefunden werden konnte.
	 * @throws UnknownSolverException Wenn eine unbekannte Methode zur
	 *             Generierung der Priorisierung übergeben wurde.
	 */
	Optional<PrioritisationDTO> create(Client client, String projectname, PrioritisationDTO prioritisation)
			throws NotFoundException, UnknownSolverException;

	/**
	 * Liefert eine Priorisierung zum zu einem Namen zurück.
	 * @param client für den die Priorisierung geladen werden soll.
	 * @param projectname name des Projektes in dem die Priorisierung liegt.
	 * @param prioritisation name der Priorisierung innerhalb des Projektes.
	 * @return Ein DTO mit der angeforderten Priorisierung.
	 * @throws NotFoundException Wenn Projekt oder Priorisierung nicht gefunden
	 *             werden konnte.
	 */
	Optional<PrioritisationDTO> get(Client client, String projectname, String prioritisation) throws NotFoundException;

	/**
	 * Löscht eine Priorisierung.
	 * @param client für den die Priorisierung geladen werden soll.
	 * @param projectname name des Projektes in dem die Priorisierung liegt.
	 * @param prioritisation name der Priorisierung innerhalb des Projektes, die
	 *            gelöscht werden soll.
	 * @return Ein DTO mit der gelöschten Priorisierung.
	 * @throws NotFoundException Wenn Projekt oder Priorisierung nicht gefunden
	 *             werden konnte.
	 */
	Optional<PrioritisationDTO> delete(Client client, String projectname, String prioritisation)
			throws NotFoundException;

	/**
	 * Updatet eine Priorisierung in der Datenbank.
	 * @param client für den die Priorisierung geladen werden soll.
	 * @param projectname name des Projektes in dem die Priorisierung liegt.
	 * @param prioritisation name der Priorisierung innerhalb des Projektes, die
	 *            upgedatet werden soll.
	 * @return Ein DTO mit der upgedateten Priorisierung.
	 * @throws NotFoundException Wenn Projekt oder Priorisierung nicht gefunden
	 *             werden konnte.
	 */
	Optional<PrioritisationDTO> update(Client client, String projectname, PrioritisationDTO prioritisation)
			throws NotFoundException;

	/**
	 * Liefert ein Entity einer  bestimmten Priorisierung.
	 * @param client für den die Priorisierung geladen werden soll.
	 * @param projectname name des Projektes in dem die Priorisierung liegt.
	 * @param prioritisation name der Priorisierung innerhalb des Projektes, die
	 *            upgedatet werden soll.
	 * @return Das Entity einer Priorisierung innerhalb eines Optionals.
	 */
	Optional<Prioritisation> getPrioritisation(Client client, String projectname, String prioritisation);
}
