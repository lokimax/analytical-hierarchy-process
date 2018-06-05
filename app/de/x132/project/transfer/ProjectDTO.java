package de.x132.project.transfer;

import de.x132.common.AbstractDTO;

/**
 * DataTransfer Objekt zum erstellen des JSON Objektes.
 * 
 * @author Max Wick
 *
 */
public class ProjectDTO extends AbstractDTO {

	private String name;

	private String beschreibung;

	/**
	 * Standard Konstruktor, wird für die Reflection API innerhalb des
	 * PlayFrameworks benötigt um eine neue Instanz des Objektes zu esretllen.
	 */
	public ProjectDTO() {
	}

	/**
	 * Liefert den Namen des Projektes
	 * 
	 * @return Name des Projektes.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen des Projektes.
	 * 
	 * @param name
	 *            des Projektes
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Liefert die BEschreibung des Projektes.
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
}
