package de.x132.node.service;

import java.util.List;
import java.util.Optional;

import de.x132.common.exceptionhandling.ConflictException;
import de.x132.common.exceptionhandling.InternalServerException;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.node.models.Node;
import de.x132.node.transfer.NodeDTO;
import de.x132.project.models.Project;
import de.x132.user.models.Client;

/**
 * Interface um Knotenelemente zu Erstellen, Lesen, Aktualisieren und Löschen von Knotenelementen.
 * 
 * @author Max Wick
 *
 */
public interface NodeService {

	/**
	 * Liefert eine Liste von Knoten DTOs für ein Projekt zurück.
	 * @param project name des Projektes
	 * @param page Seite von der das Projekt geladen werden soll.
	 * @param count wieviele Elemente sollen pro seite angezeigt werden.
	 * @return eine Liste von DTOs.
	 * @throws InternalServerException
	 */
    List<NodeDTO> list(Project project, int page, int count) throws InternalServerException;

    /**
     * Erstellt einen Knoten innerhalb des Projektes.
     * @param project in dem der Knoten erstellt werden soll.
     * @param node dto aus dem der Knoten erstellt werden soll.
     * @return wenn der knoten erfolgreich erstellt werden konnte dann im optional das entsprechende DTO.
     * @throws ConflictException wenn ein anderer Knoten mit dem gleichen Namen bereits im Projekt existiert.
     */
    Optional<NodeDTO> create(Project project, NodeDTO node) throws ConflictException;

    /**
     * Liefert einen bestimmten Knoten aus dem Projekt.
     * @param project Name des Projektes.
     * @param nodename Name des Knoten, der geliefert werden soll.
     * @return Optional mit dem Knoten.
     * @throws NotFoundException Wenn der Knoten nicht gefunden werden konnte.
     */
    Optional<NodeDTO> getNode(Project project, String nodename) throws NotFoundException;

    /**
     * Aktualisiert einen Knoten in der Datenbank.
     * @param project Name des Projektes
     * @param nodename Name des Knotens welcher in der Datenbank liegt.
     * @param node data Transfer Objekt mit dem neuen Namen des Knotens.
     * @return den neuen Knoten aus der Datenbank.
     */
    Optional<NodeDTO> update(Project project, String nodename, NodeDTO node);
    
    /**
     * Liefert die Entity zu einem bestimmten Knoten.
     * @param Projekt in dem der Knoten ist .
     * @param nodename Knotenname
     * @return Optional mit dem Knoten.
     */
    Optional<Node> get(Project project, String nodename);
    
    /**
     * Löscht einen Knoten aus der Datenbank.
     * @param project Projekt in dem der Knoten sich befinden soll.
     * @param nodename Name des Knotenelementes, welcher gelöscht werden soll.
     * @return das DTO mit dem gelöschten Knoten.
     */
    Optional<NodeDTO> delete(Project project, String nodename);
    
    /**
     * Liefert einen Knoten über den Namen zurück.
     * @param client für den gesucht werden soll. 
     * @param projectname im welchen Projekt.
     * @param nodename Der Name des Knotens. 
     * @return Optional mit dem Knoten.
     */
	Optional<Node> getNode(Client client, String projectname, String nodename);
	
	/**
	 * Liefert eine Liste der Startknoten für ein Projekt.
	 * @param client dessen Daten durchgesucht werden sollen. 
	 * @param projectname Name des Projektes.
	 * @return eine Liste der endknoten.
	 * @throws InternalServerException
	 */
	List<Node> getStartNodes(Client client, String projectname) throws InternalServerException;
	
	/**
	 * Liefert eine Liste der Endknoten.
	 * @param client dessen Daten durchgesucht werden sollen. 
	 * @param projectname Name des Projektes.
	 * @return eine Liste der endknoten.
	 * @throws InternalServerException
	 */
	List<Node> getEndNodes(Client client, String projectname) throws InternalServerException;
}
