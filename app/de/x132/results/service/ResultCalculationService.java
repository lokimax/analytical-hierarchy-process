package de.x132.results.service;

import java.util.Optional;

import de.x132.common.business.transfer.ChildResultDTO;
import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.common.exceptionhandling.BadRequestException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.NotImplementedException;
import de.x132.user.models.Client;

/**
 * Service zum Ausführen der Bewertung.
 * 
 * @author Max Wick
 */
public interface ResultCalculationService {

	/**
	 * Löst für einen Knoten den AHP auf die Kinder.
	 * 
	 * @param client
	 *            Inhaber des Projektes-
	 * @param projectname
	 *            des Projektes.
	 * @param prioritisation
	 *            die bei der Bewertung betrachtet werden soll.
	 * @param parent
	 *            für den Elternknoten, in dem das AHP angwendet werden soll.
	 * @return Ein SolvingResultDTO container, mit den einzelnen Gewichten der
	 *         Kindknoten.
	 * @throws NotFoundException
	 *             wenn etwas nciht gefunden werden konnte.
	 * @throws BadRequestException
	 *             wenn die Prioritisation unbekannte Bewertungsmethode
	 *             beinhaltet.
	 */
	Optional<SolvingResultDTO> getLocalResults(Client client, String projectname, String prioritisation, String parent)
			throws NotFoundException, BadRequestException;

	/**
	 * Liefert Ergebnisse für einen Node bezogen auf direkten Parents.
	 * 
	 * @param client
	 *            mandat des Projektes
	 * @param projectname
	 *            des Projektes.
	 * @param prioritisationname
	 *            der Priorisierung im Projekt.
	 * @param child
	 *            für den das Ergebnis geliefert werden soll.
	 * @return Optional mit den SolvingResultDTO Container, welches die
	 *         einzelnen Einflussgrößen beinhaltet.
	 * @throws NotFoundException
	 *             wenn die Priorisierung nicht gefunden werden konnte.
	 * @throws NotImplementedException
	 *             wenn die requestierte Methode nicht implementiert ist.
	 */
	Optional<SolvingResultDTO> getInfluenceResults(Client client, String projectname, String prioritisation,
			String child) throws NotFoundException, BadRequestException, NotImplementedException;

	/**
	 * Berechnet die Prioritäteten für alle Nodes im Projekt. Lieferung eines
	 * Objektes in dem ein Key der name des Nodes steht und die einzelnen
	 * Einflüsse der darüberliegenden Knoten. Nodename, GesamtResult, Einfluesse
	 * DTO
	 * 
	 * @param client
	 *            der als Projektinhaber betrachtet wird.
	 * @param projectname
	 *            name des Projektes in dem die Knoten sich befinden.
	 * @param prioritisation
	 *            beinhaltet die Bewertungen, die zur Berechnung herangezogen
	 *            werden sollen.
	 * @return ein Objekt vom Typ {@link FullResultDTO}, welches die Resultate
	 *         aller Knoten beinhaltet.
	 * @throws NotFoundException
	 *             wenn eines der Parameter nicht in der datenbank gefunden
	 *             werden konnte.
	 * @throws BadRequestException
	 *             wenn eine falsche Bewertungsmethode requestiuert worden ist,
	 *             die nicht existirt.
	 * @throws InternalServerError
	 *             wenn intern ein Fehler passiert ist.
	 */
	Optional<FullResultDTO> getFullResult(Client client, String projectname, String prioritisation)
			throws NotFoundException, BadRequestException, InternalServerException;

	/**
	 * Identisch zu getFullResult, nur dass die resultierende Tabelle nur die
	 * Blattelemente beinhaltet.
	 * 
	 * @param client
	 *            der diese Action ausführt.
	 * @param projectname
	 *            in welchem Projektkontext soll die Action ausgeführt werden.
	 * @param prioritisation
	 *            anhand welcher Bewertungen die Berechnung durchgeführt werden
	 *            soll.
	 * @return ein Objekt vom Typ {@link FullResultDTO}, welches die Resultate
	 *         der Kindknoten beinhaltet.
	 * @throws NotFoundException
	 *             * wenn eines der Parameter nicht in der datenbank gefunden
	 *             werden konnte.
	 * @throws BadRequestException
	 *             wenn eine falsche Bewertungsmethode requestiuert worden ist,
	 *             die nicht existirt.
	 * @throws InternalServerError
	 *             wenn intern ein Fehler passiert ist.
	 */
	Optional<FullResultDTO> getChildResult(Client client, String projectname, String prioritisation)
			throws NotFoundException, BadRequestException, InternalServerException;

	
	/**
	 * Liefert die Unterstützungsgrade der Endknoten für Kindelement eines Knotens.
	 * 
	 * @param client
	 *            der diese Action ausführt.
	 * @param projectname
	 *            in welchem Projektkontext soll die Action ausgeführt werden.
	 * @param prioritisation
	 *            anhand welcher Bewertungen die Berechnung durchgeführt werden
	 * @param parentname name des Elternknoten für den die Aufstellung aufgebaut werden soll.
	 * @return Eine Aufstellung zwischen den Kindelemente und derren Unterstützungsgrade.
	 * @throws NotFoundException
	 *             * wenn eines der Parameter nicht in der datenbank gefunden
	 *             werden konnte.
	 * @throws BadRequestException
	 *             wenn eine falsche Bewertungsmethode requestiuert worden ist,
	 *             die nicht existirt.
	 * @throws InternalServerError
	 *             wenn intern ein Fehler passiert ist.
	 */
	Optional<ChildResultDTO> getResultsForChilds(Client client, String projectname, String prioritisation, String parentname)
			throws NotFoundException, BadRequestException, InternalServerException, NotImplementedException;

}
