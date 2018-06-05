package de.x132.prioritisation.service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.JPAApi;
import de.x132.common.AbstractService;
import de.x132.common.business.SolvingFactory;
import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.UnknownSolverException;
import de.x132.comparison.models.Comparison;
import de.x132.comparison.transfer.ComparisonDTO;
import de.x132.node.transfer.NodeDTO;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.prioritisation.transfer.PrioritisationDTO;
import de.x132.project.models.Project;
import de.x132.project.service.ProjectService;
import de.x132.user.models.Client;

/**
 * Konkrtete Implementierung des PrioritisationService Services.
 * @author Max Wick
 *
 */
public class PrioritisationServiceImpl extends AbstractService<PrioritisationDTO, Prioritisation> implements
		PrioritisationService {

	private JPAApi jpa;
	private ProjectService projectservice;
	private SolvingFactory solvingfactory;

	/**
	 * Injezierung der Service
	 * @param jpa für den Zugriff auf die Datenbank
	 * @param projectservice für den Zugriff auf den Projektknoten 
	 * @param solvingfactory auf den Zugriff der abstrakten Facory
	 */
	@Inject
	public PrioritisationServiceImpl(JPAApi jpa, ProjectService projectservice, SolvingFactory solvingfactory) {
		super(PrioritisationDTO.class);
		this.jpa = jpa;
		this.projectservice = projectservice;
		this.solvingfactory = solvingfactory;
	}

	/**
	 * @see PrioritisationService#list(Client, String, int, int)
	 */
	@Override
	public List<PrioritisationDTO> list(Client client, String projectname, int page, int size)
			throws InternalServerException {
		TypedQuery<Prioritisation> query = jpa.em().createNamedQuery(
				Prioritisation.FIND_ALL_PRIORITISATION_FOR_PROJECT, Prioritisation.class);
		query.setParameter("nickname", client.getNickname());
		query.setParameter("projectname", projectname);
		query.setFirstResult((page - 1) * size);
		query.setMaxResults(size);
		List<Prioritisation> prioritisations = query.getResultList();
		List<PrioritisationDTO> dtos;
		try {
			dtos = this.mapList(prioritisations);
			return dtos;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InternalServerException(e.getCause());
		}
	}

	/**
	 * @see PrioritisationService#create(Client, String, PrioritisationDTO)
	 */
	@Override
	public Optional<PrioritisationDTO> create(Client client, String projectname, PrioritisationDTO prioritisationDto)
			throws NotFoundException, UnknownSolverException {
		Optional<Project> optProject = this.projectservice.getProject(client.getNickname(), projectname);

		Project project = optProject
				.orElseThrow(() -> new NotFoundException("No Project found for the name " + projectname));

		Optional<Solver<? extends SolvingResultDTO>> optsolver = this.solvingfactory.createSolver(prioritisationDto
				.getMethode());
		Solver<? extends SolvingResultDTO> createSolver = optsolver
				.orElseThrow(() -> new UnknownSolverException(prioritisationDto.getMethode()));
		Prioritisation prioritisation = createSolver.generatePriorisation(project);
		prioritisation.setModifiedBy(project.getClient());
		mapToEntity(prioritisationDto, prioritisation);

		jpa.em().persist(prioritisation);

		mapToDto(prioritisation, prioritisationDto);

		return Optional.of(prioritisationDto);
	}

	/**
	 * @see PrioritisationService#getPrioritisation(Client, String, String)
	 */
	@Override
	public Optional<Prioritisation> getPrioritisation(Client client, String projectname, String prioritisation) {
		Query queryPrioritisation = jpa.em().createNamedQuery(Prioritisation.FIND_PRIORISATION_BY_NAME);
		queryPrioritisation.setParameter("nickname", client.getNickname());
		queryPrioritisation.setParameter("projectname", projectname);
		queryPrioritisation.setParameter("prioritisationname", prioritisation);
		Prioritisation retPrioritisation = (Prioritisation) queryPrioritisation.getSingleResult();
		return Optional.of(retPrioritisation);
	}

	/**
	 * @see PrioritisationService#get(Client, String, String)
	 */
	@Override
	public Optional<PrioritisationDTO> get(Client client, String projectname, String prioritisationName)
			throws NotFoundException {
		Optional<Prioritisation> optPrioritisation = this.getPrioritisation(client, projectname, prioritisationName);
		Prioritisation prioritisation = optPrioritisation.orElseThrow(() -> new NotFoundException(
				"Priorisiation cannot be found"));
		PrioritisationDTO retDTO = new PrioritisationDTO();
		mapToDto(prioritisation, retDTO);

		return Optional.of(retDTO);
	}

	/**
	 * @see PrioritisationService#delete(Client, String, String)
	 */
	@Override
	public Optional<PrioritisationDTO> delete(Client client, String projectname, String prioritisation)
			throws NotFoundException {
		Optional<Prioritisation> optPrio = this.getPrioritisation(client, projectname, prioritisation);
		Prioritisation toDelete = optPrio.orElseThrow(() -> new NotFoundException("Prioritisation not Found"));
		jpa.em().remove(toDelete);
		PrioritisationDTO returnDTO = new PrioritisationDTO();
		mapToDto(toDelete, returnDTO);
		return Optional.of(returnDTO);
	}

	/**
	 * @see PrioritisationService#update(Client, String, PrioritisationDTO)
	 */
	@Override
	public Optional<PrioritisationDTO> update(Client client, String projectname, PrioritisationDTO prioritisation) {
		// TODO Bis jetzt noch nicht gebraucht.
		return null;
	}

	/**
	 * @see AbstractService#mapToEntity(de.x132.common.AbstractDTO, de.x132.common.CommonInformation)
	 */
	@Override
	public void mapToEntity(PrioritisationDTO source, Prioritisation target) {
		target.setName(source.getName());
	}

	/**
	 * @see AbstractService#mapToDto(de.x132.common.CommonInformation, de.x132.common.AbstractDTO)
	 */
	@Override
	public void mapToDto(Prioritisation source, PrioritisationDTO target) {
		target.setName(source.getName());
		target.setMethode(source.getMethode());
		target.clearComparisons();
		Iterator<Comparison> comparisonIterator = source.getComparisonIterator();
		while (comparisonIterator.hasNext()) {
			Comparison next = comparisonIterator.next();
			if(!next.getLeftnode().equals(next.getRightnode())){
				ComparisonDTO dto = new ComparisonDTO();
				dto.setLeftNodeName(next.getLeftnode().getName());
				dto.setRightNodeName(next.getRightnode().getName());
				dto.setParentNodeName(next.getParent().getName());
				dto.setWeight(next.getWeight());

				target.add(dto);				
			}
		}
	}
}
