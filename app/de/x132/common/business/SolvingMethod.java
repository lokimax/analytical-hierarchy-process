package de.x132.common.business;

import de.x132.common.business.ahp.AHPSolverResultFactory;
import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.SolvingResultDTO;

/**
 * Enumeration mit den Verfahten zur generierung von Prioritäten.
 * 
 * @author Max Wick
 *
 */
public enum SolvingMethod {
	
    AHP("analytic hierarchy process", AHPSolverResultFactory.class); 
//    ANP("analytic network process", ANPSolver.class);
    
    private final String name;
    private final Class<? extends Solver<? extends SolvingResultDTO>> solver;

    /**
     * Standard Konstruktor 
     * @param name Name des Verfahrens
     * @param solver die entsprechende Klasse die vom Solver interface erbt.
     */
    SolvingMethod(final String name, final Class<? extends Solver<? extends SolvingResultDTO>> solver) {
        this.name = name;
        this.solver = solver;
    }

    /**
     * Liefert nur den Namen des Verfahrens
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Liefert die Klasse zum Verfahren.
     * @return Klasse für ein Bewertungsverfahren.
     */
    public Class<? extends Solver<? extends SolvingResultDTO>> getSolver() {
        return solver;
    }        
}

