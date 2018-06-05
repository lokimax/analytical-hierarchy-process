package de.x132.project.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.JPAApi;
import de.x132.common.AbstractService;
import de.x132.project.models.Project;
import de.x132.project.transfer.ProjectDTO;
import de.x132.user.models.Client;

/**
 * Konkrete Implentierung des Service Project Services. Über den Konstruktor
 * wird die JPA-APi injeziert um Operationen auf der Datenbank durchzuführen.
 * 
 * @author Max Wick
 *
 */
public class ProjectServiceImpl extends AbstractService<ProjectDTO, Project> implements ProjectService {

	private JPAApi api;

	/**
	 * Wird von GUICE verwendet um die JPA API zu injezieren.
	 * @param api zur Persistenzeschicht.
	 */
	@Inject
	public ProjectServiceImpl(JPAApi api) {
		super(ProjectDTO.class);
		this.api = api;
	}

	/**
	 * @see ProjectService#getProject(String, String)
	 */
	@Override
	public List<ProjectDTO> getProjects(Client client) {
		List<Project> projects = this.findAllProjectsFor(client.getNickname());
		List<ProjectDTO> projectsDtos = new ArrayList<ProjectDTO>();
		for (Project project : projects) {
			ProjectDTO projectDto = new ProjectDTO();
			mapToDto(project, projectDto);
			projectsDtos.add(projectDto);
		}
		return projectsDtos;
	}

	/**
	 * @see ProjectService#create(Client, ProjectDTO)
	 */
	@Override
	public Optional<ProjectDTO> create(Client client, ProjectDTO projectDto) {
		Project project = new Project();
		project.setModifiedBy(client);
		mapToEntity(projectDto, project);
		project.setClient(client);
		this.api.em().persist(project);
		mapToDto(project, projectDto);
		return Optional.of(projectDto);
	}

	/**
	 * @see ProjectService#get(Client, String)
	 */
	@Override
	public Optional<ProjectDTO> get(Client client, String projectname) {
		Optional<Project> optProject = this.getProject(client.getNickname(), projectname);
		if (optProject.isPresent()) {
			ProjectDTO returnDto = new ProjectDTO();
			mapToDto(optProject.get(), returnDto);
			return Optional.of(returnDto);
		}
		return Optional.empty();
	}

	/**
	 * @see ProjectService#delete(Client, String)
	 */
	@Override
	public Optional<ProjectDTO> delete(Client client, String projectname) {
		Optional<Project> optPrject = this.getProject(client.getNickname(), projectname);
		if (optPrject.isPresent()) {
			Project project = optPrject.get();
			api.em().remove(project);
			ProjectDTO returnDto = new ProjectDTO();
			mapToDto(project, returnDto);
			return Optional.of(returnDto);
		}
		return Optional.empty();
	}

	/**
	 * @see ProjectService#update(Client, String, ProjectDTO)
	 */
	@Override
	public Optional<ProjectDTO> update(Client client, String projectname, ProjectDTO projectDto) {
		Optional<Project> optPrject = this.getProject(client.getNickname(), projectname);
		if (optPrject.isPresent()) {
			Project project = optPrject.get();
			mapToEntity(projectDto, project);
			api.em().persist(project);
			mapToDto(project, projectDto);
			return Optional.of(projectDto);
		}
		return Optional.empty();
	}

	/**
	 * @see ProjectService#getProject(String, String)
	 */
	@Override
	public Optional<Project> getProject(String nickname, String projectName) {
		Query queryProject = api.em().createNamedQuery(Project.FIND_PROJECT_BY_NAME);
		queryProject.setParameter("nickname", nickname);
		queryProject.setParameter("projectname", projectName);
		Project project = (Project) queryProject.getSingleResult();
		return Optional.of(project);
	}

	/**
	 * Übersetzt aus dem Transfer Objekt Daten in die EntityKlasse
	 * @param source Data Transfer Klasse aus der die Daten in das Entity
	 *            transferiert werden sollen.
	 * @param target Entity Objekt in welches Daten aus dem DataTranfer Objekt
	 *            übersetzt werden sollen.
	 */
	@Override
	public void mapToEntity(ProjectDTO source, Project target) {
		target.setName(source.getName());
		target.setBeschreibung(source.getBeschreibung());
	}

	/**
	 * Übersetzt aus dem Entity Projekt in das DataTransfer Object
	 * @param source die entity Klasse.
	 * @param target die Zielklasse.
	 */
	@Override
	public void mapToDto(Project source, ProjectDTO target) {
		target.setName(source.getName());
		target.setBeschreibung(source.getBeschreibung());
	}

	/**
	 * Liefert alle Projekte für einen Benutzer.
	 * @param nickname des Benutzers für den die Projekte geliefert werden
	 *            sollen.
	 * @return Liste der Projekte.
	 */
	private List<Project> findAllProjectsFor(String nickname) {
		try {
			TypedQuery<Project> query = api.em().createNamedQuery(Project.FIND_ALL_PROJECTS, Project.class);
			query.setParameter("nickname", nickname);

			List<Project> projects = query.getResultList();
			return projects;
		} catch (NoResultException nre) {
			return Collections.emptyList();
		}
	}

}
