package de.x132.project.utils;

import java.util.List;
import java.util.stream.Collectors;

import de.x132.node.models.Node;
import de.x132.project.models.Project;

/**
 * Utility Klasse für einen leichteren Zugriff auf Knotenelemente.
 * 
 * @author Max Wick
 *
 */
public class ProjectUtils {

	/**
	 * Liefert die Wurzelknoten eines Projektes.
	 * @param project dessen Wurzelknoten geliefert werden sollen.
	 * @return Wurzelknoten eines Projektes.
	 */
	public static List<Node> getStartNodes(Project project) {
		List<Node> collect = project.getNodes().stream().filter(x -> x.getIngoing().size() == 0).collect(Collectors.toList());
		return collect;
	}

	/**
	 * Liefert die Knoten dire Wurzel zu einem Knoten sind.
	 * @param project in welchem Projekt die Durchsuchung stattfinden soll.
	 * @param node Kindknoten dessen Wurzelknoten geliefer werden sollen.
	 * @return Wurzelknoten für ein Kindknoten
	 */
	public static List<Node> getInboundNodes(Project project, Node node) {
		List<Node> collect = node.getIngoing().stream().map(x -> {return x.getSourcenode();}).collect(Collectors.toList());
		return collect;
	}
	
	/**
	 * Liefert die Kindelemente zu einem Knoten.
	 * @param project in dem die Kindelemente durchsuchte werden sollen.
	 * @param node zu dem die Kindelemente geleiefert werden sollen.
	 * @return Liste der  Kindeelementen.
	 */
	public static List<Node> getOutboundNodes(Project project, Node node) {
		List<Node> collect = node.getOutgoing().stream().map(x -> {return x.getTargetnode();}).collect(Collectors.toList());
		return collect;
	}

}
