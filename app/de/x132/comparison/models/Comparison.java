package de.x132.comparison.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.x132.common.CommonInformation;
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;

/**
 * Stellt einen paarweisen Vergleich dar zu einem Elternelement und zwischen
 * zwei Kindknoten.
 * @author Max Wick
 *
 */
@Entity
@Table(name = "comparison")
@NamedQueries({ @NamedQuery(name = Comparison.FIND_COMPARISON, query = "SELECT comparison from Comparison comparison "
		+ "WHERE comparison.prioritisation.project.client.nickname = :nickname "
		+ "AND comparison.prioritisation.project.name = :project "
		+ "AND comparison.prioritisation.name = :prioritisation "
		+ "AND comparison.parent.name= :parentnode AND "
		+ "((comparison.leftnode.name = :leftnode AND comparison.rightnode.name = :rightnode) OR (comparison.leftnode.name = :rightnode AND comparison.rightnode.name = :leftnode))") })
public class Comparison extends CommonInformation implements Comparable<Comparison> {

	public static final String FIND_COMPARISON = "Comparison.findComparison";

	@Id
	@SequenceGenerator(name = "comparison_gen", sequenceName = "comparison_seq")
	@GeneratedValue(generator = "comparison_gen")
	private Long id;

	@JoinColumn(name = "left_node_id", nullable = false)
	@ManyToOne(targetEntity = Node.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private Node leftnode;

	@JoinColumn(name = "right_node_id", nullable = false)
	@ManyToOne(targetEntity = Node.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private Node rightnode;

	@JoinColumn(name = "parent_node_id", nullable = false)
	@ManyToOne(targetEntity = Node.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private Node parent;

	@JoinColumn(name = "priorization_id", nullable = false)
	@ManyToOne(targetEntity = Prioritisation.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	private Prioritisation prioritisation;

	@Column
	private Integer weight;

	@Override
	public int compareTo(Comparison o) {
		int compareToParent = this.parent.getName().compareTo(o.getParent().getName());
		if (compareToParent != 0)
			return compareToParent;

		int compareToLeft = this.leftnode.getName().compareTo(o.getLeftnode().getName());
		if (compareToLeft != 0)
			return compareToLeft;

		int compareRight = this.rightnode.getName().compareTo(o.getRightnode().getName());
		if (compareRight != 0)
			return compareRight;

		return 0;
	}

	public void setLeftNode(Node leftnode) {
		this.setLeftnode(leftnode);
	}

	public void setRightNode(Node rightnode) {
		this.setRightnode(rightnode);
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public Node getLeftnode() {
		return leftnode;
	}

	public void setLeftnode(Node leftnode) {
		this.leftnode = leftnode;
	}

	public Node getRightnode() {
		return rightnode;
	}

	public void setRightnode(Node rightnode) {
		this.rightnode = rightnode;
	}

	public int getWeight() {
		if (weight == null) {
			return 0;
		}
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Prioritisation getPrioritisation() {
		return prioritisation;
	}

	public void setPrioritisation(Prioritisation prioritisation) {
		this.prioritisation = prioritisation;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.leftnode.getName() == null) ? 0 : this.leftnode.getName().hashCode());
		result = prime * result + ((this.parent.getName() == null) ? 0 : this.parent.getName().hashCode());
		result = prime * result + ((this.rightnode.getName() == null) ? 0 : this.rightnode.getName().hashCode());
		return result;
	}

}
