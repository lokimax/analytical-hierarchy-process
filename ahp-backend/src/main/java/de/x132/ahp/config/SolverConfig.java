package de.x132.ahp.config;

import de.x132.ahp.core.solver.AHPSolver;
import de.x132.ahp.core.solver.Solver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolverConfig {

    @Bean
    public Solver ahpSolver() {
        return new AHPSolver();
    }
}
