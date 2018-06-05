package de.x132.node.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.x132.common.CommonInformation;
import de.x132.connection.models.Connection;
import de.x132.project.models.Project;

/**
 * Entity für einen Knotenelement.
 * @author Max Wick
 *
 */
@Entity
@Table(name = "Node", uniqueConstraints={@UniqueConstraint(columnNames={"project_id", "name"})})
@NamedQueries({
        @NamedQuery(name = Node.FIND_ALL_NODES_FOR_PROJECT, query = "SELECT node from Node node where node.project.name = :projectname AND node.project.client.nickname = :nickname"),
        @NamedQuery(name = Node.FIND_NODE_BY_NAME, query = "SELECT node from Node node where node.project.name = :projectname AND node.project.client.nickname = :nickname AND node.name = :nodename"),
        @NamedQuery(name = Node.FIND_STARTNODES, query= "Select node FROM Node node WHERE node.project.name = :projectname AND node.project.client.nickname = :nickname AND node.ingoing IS EMPTY"),
        @NamedQuery(name = Node.FIND_ENDNODES, query= "Select node FROM Node node WHERE node.project.name = :projectname AND node.project.client.nickname = :nickname AND node.outgoing IS EMPTY")
})
public class Node extends CommonInformation implements Comparable<Node> {

    public static final String FIND_ALL_NODES_FOR_PROJECT = "Node.findAllNodesForProject";

    public static final String FIND_NODE_BY_NAME = "Node.findNodeByName";
    
    public static final String FIND_STARTNODES = "Node.findStartnodes";
    
    public static final String FIND_ENDNODES = "Node.findEndnodes";

    @JoinColumn(name = "project_id")
    @ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Project project;

    @Id
    @SequenceGenerator(name = "node_gen", sequenceName = "node_seq")
    @GeneratedValue(generator = "node_gen")
    private Long id;

    @OneToMany(mappedBy= "sourcenode", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
    private List<Connection> outgoing;
    
    @OneToMany(mappedBy= "targetnode", fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
    private List<Connection> ingoing;
    
    @Override
	public String toString() {
		return "Node [name=" + name + "]";
	}

	@Column(nullable = false)
    private String name;

	@Column(name = "content", length = 10000)
    private String content;

	@Column(name = "beschreibung", length = 10000)
    private String beschreibung;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int compareTo(Node other) {
        return this.getName().compareTo(other.getName());
    }

	public List<Connection> getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(List<Connection> outgoing) {
		this.outgoing = outgoing;
	}

	public List<Connection> getIngoing() {
		return ingoing;
	}

	public void setIngoing(List<Connection> ingoing) {
		this.ingoing = ingoing;
	}
}
