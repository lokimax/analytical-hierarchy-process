package de.x132.connection.transfer;

import de.x132.common.AbstractDTO;

/**
 * Data Transfer Object von Connection.
 * @author Max Wick
 *
 */
public class ConnectionDTO extends AbstractDTO {

	private String targetnode;
	private String sourcenode;

	/**
	 * Setzt den Zielknoten.
	 * @param targetnode Zielknoten.
	 */
	public void setTargetnode(String targetnode) {
		this.targetnode = targetnode;
	}

	/**
	 * Liefert den Zeilknoten.
	 * @return Zielknoten der Verbindung.
	 */
	public String getTargetnode() {
		return this.targetnode;
	}

	/**
	 * Liefert den Quellknoten.
	 * @return name des Quellknotens.
	 */
	public String getSourcenode() {
		return sourcenode;
	}

	/**
	 * Setzt den Quellknoten für diese Connection.
	 * @param sourcenode Quellknoten.
	 */
	public void setSourcenode(String sourcenode) {
		this.sourcenode = sourcenode;
	}

}
