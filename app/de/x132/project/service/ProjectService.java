package de.x132.project.service;

import java.util.List;
import java.util.Optional;

import de.x132.project.models.Project;
import de.x132.project.transfer.ProjectDTO;
import de.x132.user.models.Client;

/**
 * Interface für den Service um Projekte zu erstellen, zu lesen, updaten und zu
 * löschen.
 * 
 * @author Max Wick
 *
 */
public interface ProjectService {

	/**
	 * Liefert alle Projekt in DTOs in Form einer Liste.
	 * 
	 * @param client
	 *            für den das Projekt erstellt werden soll.
	 * @return Liste mit den Projekten als DTOs
	 */
	List<ProjectDTO> getProjects(Client client);

	/**
	 * Erstellt einen neues Projekt anhand des übergeben DTOs.
	 * 
	 * @param client
	 *            für den das Projekt erstellt werden soll.
	 * @param project
	 *            DTO mit Metainforamtionen zu dem Projekt.
	 * @return ein Optional mit einem womöglichen Projekt.
	 */
	Optional<ProjectDTO> create(Client client, ProjectDTO project);

	/**
	 * Liefert ein bestimmtes Projekt anhand des projektnamen.
	 * 
	 * @param client
	 *            zu dem das PRojekt geliefert werden soll.
	 * @param projectname
	 *            Name des Projektes.
	 * @return ein Optional mit einem womöglichen Projekt.
	 */
	Optional<ProjectDTO> get(Client client, String projectname);

	/**
	 * Löscht ein Projekt aus der Datenbank.
	 * 
	 * @param client
	 *            zu dem das Projekt gehört.
	 * @param projectname
	 *            name des Projektes.
	 * @return ein Optional mit einem DTOs des gelöschten Projektes.
	 */
	Optional<ProjectDTO> delete(Client client, String projectname);

	/**
	 * Updatet ein Projekt innerhalb der Datenbank.
	 * 
	 * @param client
	 *            zu dem das Projekt erneuert werden soll.
	 * @param projectname
	 *            alter Name des Projektes, falls der Name des Projektes
	 *            geändert worden ist.
	 * @param project
	 *            das Transfer Objekt mit den neuen Daten.
	 * @return das Transferobjekt, nach dem das Updaten geschehen ist.
	 */
	Optional<ProjectDTO> update(Client client, String projectname, ProjectDTO project);

	/**
	 * Liefert einen bestimmtes Projekt zu einem Client mit dem nickname. Diese
	 * Funktion ist für die interne Verarbeitung gedacht.
	 * 
	 * @param nickname
	 *            des Clients.
	 * @param projectName
	 *            Name des Projektes.
	 * @return ein Optional mit einer Entity zu dem Projekt.
	 */
	Optional<Project> getProject(String nickname, String projectName);
}
