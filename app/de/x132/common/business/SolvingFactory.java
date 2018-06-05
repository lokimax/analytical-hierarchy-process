package de.x132.common.business;

import java.util.Optional;

import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.SolvingResultDTO;

/**
 * Interface zur abstrakten Factory zur Erstellunge von Bewertungsmethode.
 * @author Max Wick
 *
 */
public interface SolvingFactory {

	/**
	 * Erstellt die eine Fabrik für die entsprechende Bewertungsmethode.
	 * @param methode Bewertungsmethode.
	 * @return ein Optional mit der Bewertungsmethode, ansonsten null.
	 */
	Optional<Solver<? extends SolvingResultDTO>> createSolver(SolvingMethod methode);

}
