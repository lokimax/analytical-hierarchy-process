package de.x132.common.business.ahp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import play.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.x132.common.business.SolvingMethod;
import de.x132.common.business.ahp.transfer.AHPResultDTO;
import de.x132.common.business.core.SingleResult;
import de.x132.common.business.core.Solver;
import de.x132.common.business.transfer.FullResultDTO;
import de.x132.common.business.transfer.SingleResultDTO;
import de.x132.common.business.transfer.SolvingResultDTO;
import de.x132.comparison.models.Comparison;
import de.x132.connection.models.Connection;
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.project.models.Project;
import de.x132.project.utils.ProjectUtils;

/**
 * Die Implementierung des Solver Interfaces für AHP, Die Ergebnisse werden
 * innerhalb des AHPResultDTO für die Aufrufer festgehalten.
 * 
 * @author Max Wick
 *
 */
public class AHPSolverResultFactory implements Solver<AHPResultDTO> {

	SolvingMethod solvingMethode = SolvingMethod.AHP;

	/**
	 * Generiert eine Prioritisierung für ein Projekt.
	 */
	@Override
	public Prioritisation generatePriorisation(Project project) {
		Prioritisation comparisation = new Prioritisation();
		comparisation.setMethode(solvingMethode);
		comparisation.add(fillComparisons(project, comparisation));
		comparisation.setProject(project);
		return comparisation;
	}

	/**
	 * Erstellt eine Liste der Vergleiche für eine Priorisierung. Erstellt
	 * intern eine lookupMatrix und extrahiert anschließend die Vergleiche.
	 * 
	 * @param project welches herangezogen werden soll.
	 * @param priorization Priorisierung die befüllt wird.
	 * @return eine Liste der über alle Vergleiche.
	 */
	private List<Comparison> fillComparisons(Project project, Prioritisation priorization) {
		Table<Node, Node, Connection> table = HashBasedTable.create();
		Iterator<Connection> connections = project.getConnections();
		while (connections.hasNext()) {
			Connection connection = (Connection) connections.next();
			table.put(connection.getSourcenode(), connection.getTargetnode(), connection);
		}
		return this.getComparisons(table, priorization);
	}

	/**
	 * Liefert eine Liste von Vergleichen die für die Priorisierung notwendig
	 * sind.
	 * 
	 * @param Tabelle zwischen Knoten und den Verbindungen zwischen denen.
	 * @param priorization für welche Priorisierung sollen die Vergleiche
	 *            erstellt werden.
	 * @return eine Liste von Vergleichen die notwendig sind um AHP auf das
	 *         Aktuelle projekt durchzuführen.
	 */
	private List<Comparison> getComparisons(Table<Node, Node, Connection> table, Prioritisation priorization) {
		List<Comparison> comparisons = new ArrayList<Comparison>();
		Map<Node, Map<Node, Connection>> rowMap = table.rowMap();
		Set<Entry<Node, Map<Node, Connection>>> entrySet = rowMap.entrySet();
		for (Entry<Node, Map<Node, Connection>> entry : entrySet) {
			comparisons.addAll(createComparisons(entry, priorization));
		}
		Collections.sort(comparisons);
		return comparisons;
	}

	/**
	 * Erstellt intern für einen Elternknoten die notwendigen Vergleiche.
	 * 
	 * @param entry Parentknoten- Kinderknoten Pair für den die vErgleiche
	 *            erstellt werden sollen.
	 * @param priorization auf die die Vergleiche referenzieren sollen.
	 * @return eine Liste der Vergleiche für einen Elternknoten und dessen
	 *         Kinder.
	 */
	private Collection<? extends Comparison> createComparisons(Entry<Node, Map<Node, Connection>> entry,
			Prioritisation priorization) {
		Node parent = entry.getKey();
		Node[] array = entry.getValue().keySet().toArray(new Node[0]);
		Collection<Comparison> list = new ArrayList<Comparison>();
		for (int i = 0; i < array.length; i++) {
			for (int j = i; j < array.length; j++) {
				Comparison comparison = new Comparison();
				if (array[i].compareTo(array[j]) > 0) {
					comparison.setLeftNode(array[j]);
					comparison.setRightNode(array[i]);
				} else {
					comparison.setLeftNode(array[i]);
					comparison.setRightNode(array[j]);
				}
				comparison.setParent(parent);
				comparison.setPrioritisation(priorization);
				list.add(comparison);
			}
		}

		return list;
	}

	/**
	 * @see Solver#getSolvingResultFor(Node, Prioritisation) Erstellt eine AHP
	 *      Asuwertung für einen einzelnen Knoten anhand der Priorisierung.
	 */
	@Override
	public AHPResultDTO getSolvingResultFor(Node node, Prioritisation prioritisation) {
		AHPNode ahpNode = new AHPNode(node, prioritisation);
		Logger.info(ahpNode.toString());
		;
		AHPResultDTO dto = new AHPResultDTO();
		dto.setParentNodeName(ahpNode.getParent().getName());
		dto.setCi(ahpNode.getCi());
		dto.setCr(ahpNode.getCr());
		dto.setConsistent(ahpNode.isConsistent());
		List<SingleResultDTO> collect = ahpNode.singleResult().stream().map(t -> {
			SingleResultDTO singleResultDTO = new SingleResultDTO();
			singleResultDTO.setNodename(t.getNodename());
			singleResultDTO.setValue(t.getBigDecimalValue());
			return singleResultDTO;
		}).collect(Collectors.toList());
		dto.setSingleResults(collect);

		return dto;
	}

	/**
	 * @see Solver#getSolvingResultFor(Node, Prioritisation) Liefert Ergebnis
	 *      für einen einzigen Startknoten anhand der Priorisierung.
	 */
	@Override
	public FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation) {
		Map<Node, BigDecimal> priorities = createPriorityMap(startNodes, prioritisation);
		FullResultDTO resultDTO = new FullResultDTO();
		List<SingleResultDTO> collect = priorities.entrySet().stream().map(t -> {
			SingleResultDTO singleResultDTO = new SingleResultDTO();
			singleResultDTO.setNodename(t.getKey().getName());
			singleResultDTO.setValue(t.getValue());
			return singleResultDTO;
		}).collect(Collectors.toList());
		resultDTO.setResults(collect);
		return resultDTO;
	}

	/**
	 * @see Solver#getSolvingResult(List, Prioritisation, List)
	 */
	@Override
	public FullResultDTO getSolvingResult(List<Node> startNodes, Prioritisation prioritisation, List<Node> endNodes) {
		Map<Node, BigDecimal> priorities = createPriorityMap(startNodes, prioritisation);
		FullResultDTO resultDTO = new FullResultDTO();
		Predicate<? super Entry<Node, BigDecimal>> containsInEndNodes = c -> endNodes.stream().anyMatch(
				e -> c.getKey().equals(e));
		List<SingleResultDTO> collect = priorities.entrySet().stream().filter(containsInEndNodes).map(t -> {
			SingleResultDTO singleResultDTO = new SingleResultDTO();
			singleResultDTO.setNodename(t.getKey().getName());
			singleResultDTO.setValue(t.getValue());
			return singleResultDTO;
		}).collect(Collectors.toList());
		collect.sort((x, y) -> y.getValue().compareTo(x.getValue()));
		resultDTO.setResults(collect);
		resultDTO.setParentNode(startNodes.get(0).getName());
		return resultDTO;
	}

	/**
	 * Liefert umgekehrte Priorisierung, das heißt welche Einflüsse hatten auf
	 * eine Alternative.
	 */
	@Override
	public SolvingResultDTO getInfluenceResult(Node node, Prioritisation prioritisation) {
		List<Node> inBoundNodes = ProjectUtils.getInboundNodes(prioritisation.getProject(), node);
		List<SingleResultDTO> collect = new ArrayList<>();
		for (Node inboundNode : inBoundNodes) {
			AHPNode ahpNode = new AHPNode(inboundNode, prioritisation);
			SingleResult priorityFor = ahpNode.getPriorityFor(node);
			SingleResultDTO singleResultDTO = new SingleResultDTO();
			singleResultDTO.setNodename(inboundNode.getName());
			singleResultDTO.setValue(priorityFor.getBigDecimalValue());
			collect.add(singleResultDTO);
		}
		SolvingResultDTO solving = new SolvingResultDTO();

		solving.setSingleResults(collect);
		return solving;
	}

	/**
	 * Erstellt eine Liste zwischen Knoten und desseen Priorität über ein ganzes
	 * Netzwerk mit x startKnoten.
	 * 
	 * @param startNodes Startknoten.
	 * @param prioritisation Priorisierung.
	 * @return eine Map zwischen Knoten und dessen Priorisierung innerhalb des
	 *         Netztwerkes.
	 */
	private Map<Node, BigDecimal> createPriorityMap(List<Node> startNodes, Prioritisation prioritisation) {
		Map<Node, AHPNode> results = new HashMap<>();
		ArrayList<Node> visitedNodes = new ArrayList<>();
		Map<Node, BigDecimal> fullResult = createInitialPriorityMap(startNodes);
		Queue<Node> nodeQueue = createInitialQueue(startNodes);
		while (!nodeQueue.isEmpty()) {
			Node poppedNode = nodeQueue.poll();
			visitedNodes.add(poppedNode);
			BigDecimal fullResultSum = sumIncomingWeights(results, fullResult, poppedNode);
			fullResult.put(poppedNode, fullResultSum);

			if (poppedNode.getOutgoing() != null && poppedNode.getOutgoing().size() > 0) {
				AHPNode ahpNode = new AHPNode(poppedNode, prioritisation);
				results.put(poppedNode, ahpNode);
				offerChilds(visitedNodes, nodeQueue, poppedNode);
			}
		}
		return fullResult;
	}

	/**
	 * Befüllt in Liste die bereits besuchten Knoten.
	 * 
	 * @param visitedNodes Liste der Besuchten knoten.
	 * @param nodes die noch verarbeitet werden müssen.
	 * @param poppedNode der Knoten, dessen Kinder in die Queue geladen werden
	 *            sollen.
	 */
	private void offerChilds(ArrayList<Node> visitedNodes, Queue<Node> nodes, Node poppedNode) {
		for (Connection connection : poppedNode.getOutgoing()) {
			if (!visitedNodes.contains(connection.getTargetnode()) && !nodes.contains(connection.getTargetnode())) {
				nodes.offer(connection.getTargetnode());
			}
		}
	}

	/**
	 * Berechnet die Summe der eingehenden Knoten.
	 * 
	 * @param ahpNodes berechnete AHP Knoten.
	 * @param calcedResults Map zwischen berechnete Results und
	 * @param currentNode der aktuell betrachtete Knoten.
	 * @return ein Gewicht für den einen Knoten.
	 */
	private BigDecimal sumIncomingWeights(Map<Node, AHPNode> ahpNodes, Map<Node, BigDecimal> calcedResults,
			Node currentNode) {
		if (hasIncomingConnections(currentNode)) {
			BigDecimal fullResultSum = BigDecimal.ZERO;
			for (Connection connection : currentNode.getIngoing()) {
				if(ahpNodes.containsKey(connection.getSourcenode())){
					AHPNode ahpNode = ahpNodes.get(connection.getSourcenode());
					BigDecimal parentValue = calcedResults.get(connection.getSourcenode());
					BigDecimal priorityInParent = ahpNode.getPriorityFor(currentNode).getBigDecimalValue();
					if(parentValue.compareTo(BigDecimal.ZERO) > 0){
						fullResultSum = fullResultSum.add(priorityInParent.multiply(parentValue));
					} else {
						fullResultSum = fullResultSum.add(priorityInParent);
					}
				}
			}
			return fullResultSum;
		}
		return BigDecimal.ONE;
	}

	private boolean hasIncomingConnections(Node currentNode) {
		return currentNode.getIngoing() != null && currentNode.getIngoing().size() > 0;
	}

	/**
	 * erstellt die initiale Queue über die dann iteriert wird, das können
	 * meherere Wurzelknoten sein.
	 * 
	 * @param startNodes wurzelknoten.
	 * @return eine Queue mit Wurzelknoten.
	 */
	private Queue<Node> createInitialQueue(List<Node> startNodes) {
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.addAll(startNodes);
		return nodes;
	}

	/**
	 * Erstellt eine initiale Map zwischen Knoten und dessen Gewichte.
	 * 
	 * @param startNodes die initial hinzugefügt werden müssen.
	 * @return eine Map zwischen den initialen Knoten und deren Gewicht.
	 */
	private Map<Node, BigDecimal> createInitialPriorityMap(List<Node> startNodes) {
		Map<Node, BigDecimal> fullResult = new HashMap<>();
		startNodes.forEach((x) -> fullResult.put(x, BigDecimal.ONE));
		return fullResult;
	}

}
