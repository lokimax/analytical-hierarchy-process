package de.x132.connection.service;

import java.util.List;
import java.util.Optional;

import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.connection.transfer.ConnectionDTO;
import de.x132.user.models.Client;

/**
 * Interface für den Service um Verbindungen auf dem Server zu erstellen, lesen,
 * editieren oder zu löschen.
 * @author Max Wick
 *
 */
public interface ConnectionService {

	/**
	 * Liefert eine Liste von Verbindungen.
	 * @param client für welchen die Verbindungen gelesen werden sollen.
	 * @param projectname Name des Projektes, in dem die Verbindung liegt.
	 * @param page Seite der Liste
	 * @param size Anzahl der Elemente die dargestellt werden sollen.
	 * @return mit der Liste von Data Transfer Objects der Verbidnungen.
	 * @throws InternalServerException wenn die Abfrage nicht funktioniert hat.
	 */
	List<ConnectionDTO> getConnections(Client client, String projectname, int page, int size)
			throws InternalServerException;
	
	/**
	 * Liefert eine Liste von Verbindungen, die von einem Knoten ausgehen.
	 * @param client für welchen die Verbindungen gelesen werden sollen.
	 * @param projectname Name des Projektes, in dem die Verbindung liegt.
	 * @param startNode	name des Startknoten von dem die resultierenden gelistet werden sollen.
	 * @return mit der Liste von Data Transfer Objects der Verbidnungen.
	 * @throws InternalServerException wenn die Abfrage nicht funktioniert hat.
	 */
	List<ConnectionDTO> getConnections(Client client, String projectname, String startnode)
			throws InternalServerException;

	/**
	 * Erstellt eine neue Verbindung.
	 * @param client für den die Verbindung erstellt werden soll.
	 * @param projectName innerhalb des Projektes, in dem die Verbindung
	 *            erstellt werden soll.
	 * @param connection das DTO der Verbindung die innerhalb der Datenbank
	 *            abgelegt werden soll.
	 * @return ein DTO mit gemappten Feldern aus der Datenbank.
	 */
	Optional<ConnectionDTO> create(Client client, String projectName, ConnectionDTO connection);

	/**
	 * Liefert eine konkrete Verbindung.
	 * @param client für welchen die Verbindung gelesen werden soll.
	 * @param projectname Name des Projektes, in dem die Verbindung liegt.
	 * @param startNode Elternknoten der Verbdindung
	 * @param finishNode Kindknoten der Verbindung.
	 * @return ein Optional mit dem DTO der Verbindung.
	 */
	Optional<ConnectionDTO> getConnection(Client client, String projectname, String startNode, String finishNode);

	/**
	 * Löscht eine Verbindung innerhlab eines Projektes.
	 * @param client in dessen Projekten die Verbindung gelöscht werden soll.
	 * @param projectname Name des Projektes von dem Client bei dem die
	 *            Verbindung gelöscht werden soll.
	 * @param startNode Elternknoten der Verbdindung
	 * @param finishNode Kindknoten der Verbindung.
	 * @return ein DTO zu der gelöschten Verbindung.
	 */
	Optional<ConnectionDTO> delete(Client client, String projectname, String startNode, String finishNode);
}
