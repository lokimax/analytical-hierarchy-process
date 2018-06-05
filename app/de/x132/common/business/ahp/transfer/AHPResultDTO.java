package de.x132.common.business.ahp.transfer;

import java.math.BigDecimal;

import de.x132.common.business.transfer.SolvingResultDTO;

/**
 * TransferObjekt, welches zur Generierung von Json verwendet wird. Objekte
 * dieser Klasse beinhalten die Informationen, die durch AHP berechnet wurden
 * und in einem AHP Node sich befinden.
 * 
 * @author Max Wick
 *
 */
public class AHPResultDTO extends SolvingResultDTO {

	private String parentNodeName;
	private Boolean consistent;
	private BigDecimal ci;
	private BigDecimal cr;

	/**
	 * Liefert den Namen des Elternknoten.
	 * 
	 * @return Name des Elternknoten.
	 */
	public String getParentNodeName() {
		return parentNodeName;
	}

	/**
	 * Setzt den Namen des Elternknoten.
	 * 
	 * @param parenNodeName
	 *            Name des Elternknoten.
	 */
	public void setParentNodeName(String parenNodeName) {
		this.parentNodeName = parenNodeName;
	}

	/**
	 * Gibt an ob die Bewertungen zum Elternknoten konsistent sind.
	 * 
	 * @return True wenn die Bewertungen konsistent sind. False wenn die
	 *         Bewertung inkonsistent sind.
	 */
	public Boolean getConsistent() {
		return consistent;
	}

	/**
	 * Setzt die Konsistenz für die Bewertungen zum Elternknoten.
	 * 
	 * @param consistent
	 *            True wenn die Bewertungen konsistent sind. False wenn die
	 *            Bewertung inkonsistent sind.
	 */
	public void setConsistent(Boolean consistent) {
		this.consistent = consistent;
	}

	/**
	 * Liefert den Konsistenzindex der Evolutionsmatrix für den Elternknoten.
	 * 
	 * @return Konsistenzindex
	 */
	public BigDecimal getCi() {
		return ci;
	}

	/**
	 * Setzt den Konsistenzindex der Evolutionsmatrix für den Elternknoten.
	 * 
	 * @param ci
	 *            Konsistenzindex der Evolutionsmatrix für den Elternknoten.
	 */
	public void setCi(BigDecimal ci) {
		this.ci = ci;
	}

	/**
	 * Liefert den Konsistenzwert der Evolutionsmatrix für den Elternknoten.
	 * 
	 * @return Konsistenzwert der Evolutionsmatrix für den Elternknoten.
	 */
	public BigDecimal getCr() {
		return cr;
	}

	/**
	 * Setzt den Konsistenzwert der Evolutionsmatrix für den Elternknoten.
	 * 
	 * @param cr
	 *            der Konsistenzwert der Evolutionsmatrix für den Elternknoten.
	 */
	public void setCr(BigDecimal cr) {
		this.cr = cr;
	}

}
