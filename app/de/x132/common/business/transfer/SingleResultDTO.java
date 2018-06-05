package de.x132.common.business.transfer;

import java.math.BigDecimal;

import de.x132.common.business.core.SingleResult;

/**
 * Ein DTO zu SingleResult @link {@link SingleResult}
 * @author Max Wick
 *
 */
public class SingleResultDTO {

	/**
	 * Name des Knotens
	 */
	private String nodename;
	
	/**
	 * Dazugehöriger Value
	 */
	private BigDecimal value;

	/**
	 * Liefert den Namen des Knotens.
	 * @return name des Knotes für den SingleResult.
	 */
	public String getNodename() {
		return nodename;
	}

	/**
	 * Setzt den Namen des Knotens.
	 * @param nodename Knotenname.
	 */
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	/**
	 * Liefert den Unterstützungsgrad zum Knoten.
	 * @return Unterstützungsgrad.
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Setzt den Unterstützungsgrad.
	 * @param value unterstützungsgrad.
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
