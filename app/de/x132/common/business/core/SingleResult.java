package de.x132.common.business.core;

import java.math.BigDecimal;

/**
 * Eine Klasse zum Darstellen von Priorisierungen, wird als interne
 * Datenhaltungsklasse genutzt.
 * 
 * @author Max Wick
 *
 */
public class SingleResult {

	/**
	 * Name des Knotens.
	 */
	private String nodename;

	/**
	 * Die entsprechende Gewichtung für den Knoten.
	 */
	private BigDecimal bigDecimalValue;

	/**
	 * Standardkonstruktor.
	 */
	public SingleResult() {

	}

	/**
	 * Erstellt ein neues Objekt.
	 * @param nodename Name des Knotens.
	 * @param bigDecimalValue dazugehöriger Unterstützungsgrad.
	 */
	public SingleResult(String nodename, BigDecimal bigDecimalValue) {
		this.setNodename(nodename);
		this.setBigDecimalValue(bigDecimalValue);
	}

	/**
	 * Liefert den Namen des Knotens.
	 * @return name des Knotens.
	 */
	public String getNodename() {
		return nodename;
	}

	/**
	 * Setzt den Namen des Knotens.
	 * @param nodename der gesetzt werden soll.
	 */
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public BigDecimal getBigDecimalValue() {
		return bigDecimalValue;
	}

	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}

}
