package de.x132.connection.models;

import javax.persistence.CascadeType;
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
import javax.persistence.UniqueConstraint;

import de.x132.common.CommonInformation;
import de.x132.node.models.Node;
import de.x132.project.models.Project;

/**
 * Entity Klasse für eine Verbindung.
 * @author Max Wick
 *
 */
@Entity
@Table(name = "Connection", uniqueConstraints={@UniqueConstraint(columnNames={"project_id", "sourcenode_id", "targetnode_id"})})
@NamedQueries({
		@NamedQuery(name = Connection.FIND_CONNECTION, query = "SELECT connection from Connection connection where connection.project.name = :projectname AND connection.project.client.nickname = :nickname AND connection.sourcenode.name = :sourcenode AND connection.targetnode.name = :targetnode"),
		@NamedQuery(name = Connection.FIND_ALL_CONNECTION_FOR_STARTNODE, query = "SELECT connection from Connection connection where connection.project.name = :projectname AND connection.project.client.nickname = :nickname AND connection.sourcenode.name = :sourcenode"),
		@NamedQuery(name = Connection.FIND_ALL_CONNECTION_FOR_PROJECT, query = "SELECT connection from Connection connection where connection.project.name = :projectname AND connection.project.client.nickname = :nickname"),
		})

public class Connection extends CommonInformation {

	public static final String FIND_ALL_CONNECTION_FOR_STARTNODE = "Connection.findAllConnectionForStartnode";

	public static final String FIND_ALL_CONNECTION_FOR_PROJECT = "Connection.findAllConnectionForProject";

	public static final String FIND_CONNECTION = "Connection.findConnection";

	@JoinColumn(name = "project_id")
	@ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private Project project;

	@Id
	@SequenceGenerator(name = "connection_gen", sequenceName = "connection_seq")
	@GeneratedValue(generator = "connection_gen")
	private Long id;

	@JoinColumn(name = "sourcenode_id")
	@ManyToOne(targetEntity = Node.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Node sourcenode;

	@JoinColumn(name = "targetnode_id")
	@ManyToOne(targetEntity = Node.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Node targetnode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Node getSourcenode() {
		return sourcenode;
	}

	public void setSourcenode(Node sourcenode) {
		this.sourcenode = sourcenode;
	}

	public Node getTargetnode() {
		return targetnode;
	}

	public void setTargetnode(Node targetnode) {
		this.targetnode = targetnode;
	}

}
