package de.x132.common.business.core;

import java.util.List;

import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.project.models.Project;

/**
 * Interface für die einzelnen Lösungsgeneratoren.
 * 
 * @author Max Wick
 *
 * @param <T> Abstrakter Resultat des Generators.
 */
public interface Solver<T extends SolvingResultDTO> {

	/**
	 * Generiert eine Prioritisation für das Entsprechende Solving.
	 * 
	 * @param project zu welchem Projekt soll das PriorisierungsTemplate erzeugt
	 *            werden.
	 * @return ein Priorisierungs Template.
	 */
	Prioritisation generatePriorisation(Project project);

	/**
	 * Liefert ein Metaobjekt mit Ergebnissen zu der Priorisierung für den einen
	 * Elternknoten.
	 * 
	 * @param node elternknoten für den das Resultat geliefert werden soll.
	 * @param prioritisation anhand welcher Vergleiche soll die Priorisierung
	 *            berechnet werden.
	 * @return Ein MetaObjekt mit einer Liste für die Priorisierung.
	 */
	T getSolvingResultFor(Node node, Prioritisation prioritisation);

	/**
	 * Liefert die Komplette Liste mit Priorisierungen über alle Knoten, die
	 * gefunden werden konnten.
	 * 
	 * @param startNodes Liste von Startknoten, ab den gerechnet werden soll.
	 * 
	 * @param prioritisation anhand welcher Vergleiche soll die Priorisierung
	 *            berechnet werden.
	 * 
	 * @return Liste mit Priorisierungsinformationen über alle Knoten.
	 */
	FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation);

	/**
	 * Liefert umgekehrte Abhängigkeiten, Einflussgrößen für einen Knoten.
	 * 
	 * @param node für den die Einflüssgrößen geliefert werden sollen.
	 * @param prioritisation anhand welcher Berechnung sollen die Einflussgrößen
	 *            geliefert werden.
	 * @return eine SolvingResultDTO mit den SingleResults der einfluss größen
	 */
	SolvingResultDTO getInfluenceResult(Node node, Prioritisation prioritisation);

	/**
	 * Liefert nur die Ergebnisse, die in der showOnlyNodes sich befinden für
	 * die Gesamtauswertung.
	 * 
	 * @param node für den die Einflüssgrößen geliefert werden sollen.
	 * @param prioritisation anhand welcher Berechnung sollen die Einflussgrößen
	 *            geliefert werden.
	 * @param showOnlyNodes auf diese Datensätze soll der Result gefiltert
	 *            werden.
	 * @return eine SolvingResultDTO mit den SingleResults der einfluss größen
	 */
	FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation, List<Node> showOnlyNodes);

}
