package de.x132.prioritisation.models;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.x132.common.CommonInformation;
import de.x132.common.business.SolvingMethod;
import de.x132.comparison.models.Comparison;
import de.x132.project.models.Project;
import de.x132.user.models.Client;

@Entity
@Table(name = "prioritisation", uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "name" }) })
@NamedQueries({
    @NamedQuery(name = Prioritisation.FIND_ALL_PRIORITISATION_FOR_PROJECT, query = "SELECT prioritisation FROM Prioritisation prioritisation WHERE prioritisation.project.client.nickname = :nickname AND prioritisation.project.name = :projectname"),
    @NamedQuery(name = Prioritisation.FIND_PRIORISATION_BY_NAME, query = "SELECT prioritisation FROM Prioritisation prioritisation WHERE prioritisation.project.client.nickname = :nickname AND prioritisation.project.name = :projectname AND prioritisation.name = :prioritisationname"),
    @NamedQuery(name = Prioritisation.GET_METHODE, query = "SELECT prioritisation.methode FROM Prioritisation prioritisation WHERE prioritisation.project.client.nickname = :nickname AND prioritisation.project.name = :projectname AND prioritisation.name = :prioritisationname"),
})
public class Prioritisation extends CommonInformation {

    public static final String FIND_ALL_PRIORITISATION_FOR_PROJECT = "Prioritisation.findAllPrioritisationForProject";

	public static final String FIND_PRIORISATION_BY_NAME = "Prioritisation.findPriorisationByName";

	public static final String GET_METHODE = "Prioritisation.getMethode";

	@JoinColumn(name = "project_id")
    @ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Project project;

    @Id
    @SequenceGenerator(name = "prioritisation_gen", sequenceName = "prioritisation_seq")
    @GeneratedValue(generator = "prioritisation_gen")
    private Long id;
    
    @Enumerated
    private SolvingMethod methode;

    @OneToMany(mappedBy="prioritisation", cascade=CascadeType.ALL)
	private List<Comparison> comparisons;
    
    @Column
    
    private String name;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SolvingMethod getMethode() {
		return methode;
	}

	public void setMethode(SolvingMethod methode) {
		this.methode = methode;
	}

	public Iterator<Comparison> getComparisonIterator() {
		return this.getComparisons().iterator();
	}

	public void add(List<Comparison> comparisons) {
		this.setComparisons(comparisons);
	}
	
	@Override
	public void setModifiedBy(Client client) {
		super.setModifiedBy(client);
		for(Comparison comparison : getComparisons()){
			comparison.setModifiedBy(client);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Comparison> getComparisons() {
		return Collections.unmodifiableList(comparisons);
	}

	public void setComparisons(List<Comparison> comparisons) {
		this.comparisons = comparisons;
	}
}
