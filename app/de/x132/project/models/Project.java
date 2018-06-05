package de.x132.project.models;

import java.util.Iterator;
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
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;
import de.x132.user.models.Client;

/**
 * Entity Klasse für das Objekt.
 * 
 * @author Max Wick
 *
 */
@Entity
@Table(name = "project", uniqueConstraints = { @UniqueConstraint(columnNames = { "client_id", "name" }) })
@NamedQueries({
		@NamedQuery(name = Project.FIND_ALL_PROJECTS, query = "SELECT project from Project project where project.client.nickname = :nickname"),
		@NamedQuery(name = Project.FIND_PROJECT_BY_NAME, query = "SELECT project from Project project where project.client.nickname = :nickname AND project.name = :projectname"),

})
public class Project extends CommonInformation {

	public static final String FIND_ALL_PROJECTS = "Project.findAllProjects";

	public static final String FIND_PROJECT_BY_NAME = "Project.findProjectByName";

	@Id
	@SequenceGenerator(name = "project_gen", sequenceName = "project_seq")
	@GeneratedValue(generator = "project_gen")
	private Long id;

	@JoinColumn(name = "client_id")
	@ManyToOne(targetEntity = Client.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private Client client;

	@Column(name = "name", length = 32)
	private String name;

	@Column(name = "beschreibung", length = 10000)
	private String beschreibung;

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Node> nodes;

	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Connection> connections;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	private List<Prioritisation> prioritisation;

	/**
	 * Standardkonstruktor wird für JPA benötigt um eine Instanz dieser Klasse
	 * zu erzeugen.
	 */
	public Project() {

	}

	/**
	 * Liefert die generierte ID des Projektes aus der Datenbank.
	 * 
	 * @return id des Projektes.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Setzt die ID des Projektes, wird zum Beispiel zum Überschreiben benötigt.
	 * 
	 * @param id
	 *            die ID des zu überschreibenen Objektes.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Liefetrt den Client, dem dieses Projekt zugewiesen ist.
	 * 
	 * @return
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Setzt den Client zum Projekt.
	 * 
	 * @param client
	 *            dem das Projekt gehört.
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Name des Projektes.
	 * 
	 * @return name des Projektes.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen des Projektes.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Liefert die Beschreibung des Projektes.
	 * 
	 * @return Beschreibung des Projektes.
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * Setzt die Beschreibung des Projektes.
	 * 
	 * @param beschreibung
	 *            des Projektes.
	 */
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	/**
	 * Liste der dazugehörigen Knoten.
	 * 
	 * @return Liste der Knoten innerhalb des Projektes.
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * Setzt die Liste der Nodes für diese Projekt.
	 * 
	 * @param nodes
	 */
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Iterator über die Verbindungen zwischen den Knoten innerhalb des
	 * Projektes.
	 * 
	 * @return Iterator über die Verbindungen innerhalb des Projektes.
	 */
	public Iterator<Connection> getConnections() {
		return connections.iterator();
	}

}
