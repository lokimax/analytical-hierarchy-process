package de.x132.results.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import play.Logger;
import de.x132.common.business.SolvingFactory;
import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.ChildResultDTO;
import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.common.exceptionhandling.BadRequestException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.common.exceptionhandling.NotImplementedException;
import de.x132.node.models.Node;
import de.x132.node.service.NodeService;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.prioritisation.service.PrioritisationService;
import de.x132.project.utils.ProjectUtils;
import de.x132.user.models.Client;

/**
 * Erstellt einen neuen Service der zur Orchestration anderer Services ist um
 * Bewertungsresultate zu berechnen. Zur Berechnung der Bewertungsresultaten
 * wird auf die {@link SolvingFactory} zugegriffen.
 * @author Max Wick
 *
 */
public class ResultCalculationServiceImpl implements ResultCalculationService {

	private PrioritisationService prioritisationsService;
	private SolvingFactory solvingFactory;
	private NodeService nodeService;

	/**
	 * Erstellt einen neuen Service der zur Orchestration anderer Services ist,
	 * zur Berechnung der Bewertungsresultaten.
	 * 
	 * @param prioritisationsService liefert die Prioriritäten, die vom Benutzer
	 *            bewertet worden sind.
	 * @param solvingFactory Factory zur erstellung der konkreten
	 *            Berechnungsservices.
	 * @param nodeService zur Berechnung von Startknoten.
	 */
	@Inject
	public ResultCalculationServiceImpl(PrioritisationService prioritisationsService, SolvingFactory solvingFactory,
			NodeService nodeService) {
		this.prioritisationsService = prioritisationsService;
		this.solvingFactory = solvingFactory;
		this.nodeService = nodeService;
	}

	/**
	 * @see ResultCalculationService#getLocalResults(Client, String, String,
	 *      String)
	 */
	@Override
	public Optional<SolvingResultDTO> getLocalResults(Client client, String projectname, String prioritisationname,
			String parent) throws NotFoundException, BadRequestException {
		Logger.info("Get local Results for " + prioritisationname);

		Optional<Prioritisation> priorisationOptional = prioritisationsService.getPrioritisation(client, projectname,
				prioritisationname);
		Node node = nodeService.getNode(client, projectname, parent).orElseThrow(
				() -> new NotFoundException("Node not found."));
		Prioritisation prioritisation = priorisationOptional.orElseThrow(() -> new NotFoundException(
				"No Prioritisation found."));
		Solver<?> solver = this.solvingFactory.createSolver(prioritisation.getMethode()).orElseThrow(
				() -> new BadRequestException("Solver is not implemented"));
		SolvingResultDTO solvingResultFor = solver.getSolvingResultFor(node, prioritisation);
		return Optional.of(solvingResultFor);
	}

	/**
	 * @throws NotImplementedException
	 * @see ResultCalculationService#getInfluenceResults(Client, String, String,
	 *      String)
	 */
	@Override
	public Optional<SolvingResultDTO> getInfluenceResults(Client client, String projectname, String prioritisationname,
			String child) throws NotFoundException, NotImplementedException {
		Logger.info("Get Inbound Results for " + prioritisationname);

		Node node = nodeService.getNode(client, projectname, child).orElseThrow(
				() -> new NotFoundException("Node not found."));
		Prioritisation prioritisation = prioritisationsService.getPrioritisation(client, projectname,
				prioritisationname).orElseThrow(() -> new NotFoundException("No Prioritisation found."));
		Solver<?> solver = this.solvingFactory.createSolver(prioritisation.getMethode()).orElseThrow(
				() -> new NotImplementedException("Solver is not implemented"));
		SolvingResultDTO solvingResultFor = solver.getInfluenceResult(node, prioritisation);
		return Optional.of(solvingResultFor);
	}

	/**
	 * @see ResultCalculationService#getFullResult(Client, String, String)
	 */
	@Override
	public Optional<FullResultDTO> getFullResult(Client client, String projectname, String prioritisationname)
			throws NotFoundException, BadRequestException, InternalServerException {
		Logger.info("Get Inbound Results for " + prioritisationname);
		Prioritisation prioritisation = prioritisationsService.getPrioritisation(client, projectname,
				prioritisationname).orElseThrow(() -> new NotFoundException("No Prioritisation found."));
		Solver<?> solver = this.solvingFactory.createSolver(prioritisation.getMethode()).orElseThrow(
				() -> new BadRequestException("Solver is not implemented"));
		List<Node> startNodes = nodeService.getStartNodes(client, projectname);
		return Optional.of(solver.getSolvingResult(startNodes, prioritisation));
	}

	/**
	 * @see ResultCalculationService#getFullResult(Client, String, String)
	 */
	@Override
	public Optional<FullResultDTO> getChildResult(Client client, String projectname, String prioritisationname)
			throws NotFoundException, BadRequestException, InternalServerException {
		Logger.info("Get Inbound Results for " + prioritisationname);
		Prioritisation prioritisation = prioritisationsService.getPrioritisation(client, projectname,
				prioritisationname).orElseThrow(() -> new NotFoundException("No Prioritisation found."));
		Solver<?> solver = this.solvingFactory.createSolver(prioritisation.getMethode()).orElseThrow(
				() -> new BadRequestException("Solver is not implemented"));
		List<Node> startNodes = nodeService.getStartNodes(client, projectname);
		List<Node> endNodes = nodeService.getEndNodes(client, projectname);
		return Optional.of(solver.getSolvingResult(startNodes, prioritisation, endNodes));
	}

	/**
	 * @see ResultCalculationService#getResultsForChilds(Client, String, String,
	 *      String)
	 */
	@Override
	public Optional<ChildResultDTO> getResultsForChilds(Client client, String projectname, String prioritisationname,
			String parentname) throws NotFoundException, BadRequestException, InternalServerException {
		Logger.info("Get Inbound Results for " + prioritisationname);
		Prioritisation prioritisation = prioritisationsService.getPrioritisation(client, projectname,
				prioritisationname).orElseThrow(() -> new NotFoundException("No Prioritisation found."));
		Solver<?> solver = this.solvingFactory.createSolver(prioritisation.getMethode()).orElseThrow(
				() -> new BadRequestException("Solver is not implemented"));

		Node node = nodeService.getNode(client, projectname, parentname).orElseThrow(
				() -> new NotFoundException("Node not found."));
		List<Node> endNodes = nodeService.getEndNodes(client, projectname);
		List<Node> outboundNodes = ProjectUtils.getOutboundNodes(prioritisation.getProject(), node);
		
		ChildResultDTO result = new ChildResultDTO();
		for(Node childnode : outboundNodes){
			FullResultDTO solvingResult = solver.getSolvingResult(Arrays.asList(childnode), prioritisation, endNodes);
			result.getSolvingResults().add(solvingResult);
		}
		
		return Optional.of(result);
	}

}
