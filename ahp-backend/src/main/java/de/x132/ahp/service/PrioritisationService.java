package de.x132.ahp.service;

import de.x132.ahp.core.dto.FullResultDTO;
import de.x132.ahp.core.solver.Solver;
import de.x132.ahp.model.Comparison;
import de.x132.ahp.model.Node;
import de.x132.ahp.model.Prioritisation;
import de.x132.ahp.model.Project;
import de.x132.ahp.repository.ComparisonRepository;
import de.x132.ahp.repository.PrioritisationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrioritisationService {

    private final PrioritisationRepository prioritisationRepository;
    private final ComparisonRepository comparisonRepository;
    private final Solver solver;

    public PrioritisationService(PrioritisationRepository prioritisationRepository,
                                  ComparisonRepository comparisonRepository,
                                  Solver solver) {
        this.prioritisationRepository = prioritisationRepository;
        this.comparisonRepository = comparisonRepository;
        this.solver = solver;
    }

    public Prioritisation createPrioritisation(Prioritisation prioritisation) {
        return prioritisationRepository.save(prioritisation);
    }

    public Optional<Prioritisation> findById(Long id) {
        return prioritisationRepository.findById(id);
    }

    public List<Prioritisation> findAllByProject(Project project) {
        return prioritisationRepository.findAllByProject(project);
    }

    public Optional<Prioritisation> findByProjectAndName(Project project, String name) {
        return prioritisationRepository.findByProjectAndName(project, name);
    }

    public Optional<Prioritisation> findByProjectClientNicknameAndProjectNameAndName(
            String clientNickname, String projectName, String prioritisationName) {
        return prioritisationRepository.findByProjectClientNicknameAndProjectNameAndName(
                clientNickname, projectName, prioritisationName);
    }

    public Prioritisation updatePrioritisation(Prioritisation prioritisation) {
        return prioritisationRepository.save(prioritisation);
    }

    public void deletePrioritisation(Long id) {
        prioritisationRepository.deleteById(id);
    }

    public List<Prioritisation> findAll() {
        return prioritisationRepository.findAll();
    }

    public Comparison addComparison(Comparison comparison) {
        return comparisonRepository.save(comparison);
    }

    public List<Comparison> findComparisonsByPrioritisation(Prioritisation prioritisation) {
        return comparisonRepository.findAllByPrioritisation(prioritisation);
    }

    public List<Comparison> findComparisonsByParent(Node parent) {
        return comparisonRepository.findAllByParent(parent);
    }

    public FullResultDTO calculateAHP(Prioritisation prioritisation, List<Node> startNodes) {
        List<Comparison> comparisons = comparisonRepository.findAllByPrioritisation(prioritisation);
        
        de.x132.ahp.core.model.Prioritisation corePrioritisation = 
            convertToCorePrioritisation(prioritisation, comparisons);
        
        List<de.x132.ahp.core.model.Node> coreStartNodes = startNodes.stream()
            .map(node -> de.x132.ahp.core.model.Node.builder()
                .name(node.getName())
                .build())
            .toList();
        
        return solver.getSolvingResult(coreStartNodes, corePrioritisation);
    }

    private de.x132.ahp.core.model.Prioritisation convertToCorePrioritisation(
            Prioritisation prioritisation, List<Comparison> comparisons) {
        
        de.x132.ahp.core.model.Project coreProject = 
            de.x132.ahp.core.model.Project.builder()
                .name(prioritisation.getProject().getName())
                .build();

        de.x132.ahp.core.model.Prioritisation corePrioritisation = 
            de.x132.ahp.core.model.Prioritisation.builder()
                .name(prioritisation.getName())
                .project(coreProject)
                .build();

        List<de.x132.ahp.core.model.Comparison> coreComparisons = comparisons.stream()
            .map(this::convertToCoreComparison)
            .toList();

        corePrioritisation.setComparisons(coreComparisons);
        
        return corePrioritisation;
    }

    private de.x132.ahp.core.model.Comparison convertToCoreComparison(Comparison comparison) {
        de.x132.ahp.core.model.Node parentNode = 
            de.x132.ahp.core.model.Node.builder()
                .name(comparison.getParent().getName())
                .build();

        de.x132.ahp.core.model.Node leftNode = 
            de.x132.ahp.core.model.Node.builder()
                .name(comparison.getLeftNode().getName())
                .build();

        de.x132.ahp.core.model.Node rightNode = 
            de.x132.ahp.core.model.Node.builder()
                .name(comparison.getRightNode().getName())
                .build();

        return de.x132.ahp.core.model.Comparison.builder()
            .parent(parentNode)
            .nodeA(leftNode)
            .nodeB(rightNode)
            .value(comparison.getWeight())
            .build();
    }
}
