package de.x132.common.business.transfer;

import java.util.List;

/**
 * Fullresult stellt ein vollständiges Result von der Priorisierungsmethode dar,
 * indem es alle priorisierende Objekte und ihren Unterstüptzungsgrad liefert.
 * 
 * @author Max Wick
 *
 */
public class FullResultDTO {

	private String parentNode;
	List<SingleResultDTO> results;

	/**
	 * Setzt die Liste von eizelnen Einträgen.
	 * @param results Liste der Resultate
	 */
	public void setResults(List<SingleResultDTO> results) {
		this.results = results;
	}

	/**
	 * Liefert eine Liste der Resultaten.
	 * @return Resultate.
	 */
	public List<SingleResultDTO> getResults() {
		return results;
	}

	/**
	 * Liefert den Elternknoten.
	 * @return Elternknoten.
	 */
	public String getParentNode() {
		return parentNode;
	}

	/**
	 * Setzt den Elternknoten.
	 * @param parentNode der Elternknoten.
	 */
	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

}
