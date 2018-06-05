package de.x132.common.business;

import java.util.Optional;

import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.SolvingResultDTO;

/**
 * Implementierung der SolvingFactory. Erstellt anhand der übergebenen Class
 * eine ResultFactory.
 * 
 * @author Max Wick
 *
 */
public class SolvingFactoryImpl implements SolvingFactory {

	/**
	 * Erstellt einen Solver für die übergebene Methode.
	 */
	public Optional<Solver<? extends SolvingResultDTO>> createSolver(SolvingMethod methode) {

		Class<? extends Solver<? extends SolvingResultDTO>> clazz = methode.getSolver();

		try {
			Solver<? extends SolvingResultDTO> solver = (Solver<? extends SolvingResultDTO>) clazz.newInstance();
			return Optional.of(solver);
		} catch (InstantiationException | IllegalAccessException e) {
			return Optional.empty();
		}
	}

}
