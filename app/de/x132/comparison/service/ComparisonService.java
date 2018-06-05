package de.x132.comparison.service;

import java.util.Optional;

import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.comparison.transfer.ComparisonDTO;
import de.x132.user.models.Client;

/**
 * Service interface um auf paarweise Vergleich zuzugreifen.
 * @author Max Wick
 *
 */
public interface ComparisonService {

	/**
	 * Liefert ein bestimmten Vergleich zurück.
	 * @param client zu dem das Projekt gehört.
	 * @param projectname zu dem die Priorisierung erstellt worden ist.
	 * @param prioritisation die Priorisierung zu der der Vergleich gehört.
	 * @param parent Elternknoten im Vergleich.
	 * @param leftnode linken Knoten im Vergleich.
	 * @param rightnode rechter Knoten im Vergleich.
	 * @return angeforderte Vergleich.
	 * @throws NotFoundException wenn der Vergleich nicht gefunden werden
	 *             konnte.
	 */
	Optional<ComparisonDTO> get(Client client, String projectname, String prioritisation, String parent,
			String leftnode, String rightnode) throws NotFoundException;

	/**
	 * Aktualisiert einen Vergleich in der Datenbank.
	 * @param client zu dem das Projekt gehört.
	 * @param projectname zu dem die Priorisierung erstellt worden ist.
	 * @param prioritisation die Priorisierung zu der der Vergleich gehört.
	 * @param comparison der Vergleich selbst, der in die Datenbank gespeichert werden soll.
	 * @return der persistierte Vergleich.
	 * @throws NotFoundException wenn der Verglecih nicht gefunden werden konnte, der upgedatet werden sollte.
	 */
	Optional<ComparisonDTO> update(Client client, String projectname, String prioritisation, ComparisonDTO comparison)
			throws NotFoundException;
}
