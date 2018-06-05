package de.x132.common.business.ahp;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Der Priority Holder hält einen Wert zwischen zwei Priorisierungen. Er wandelt
 * den Wert aus der Auswahl des Benutzers in die jeweilige Gewichtung.
 * 
 * @author Max Wick
 */
public class PriorityHolder {

	private int[] weights = new int[] { 1, 3, 5, 7, 9 };

	private BigDecimal priorityForLeft;

	private BigDecimal priorityForRight;

	private BigDecimal priorityForCenter;

	protected PriorityHolder(int index) {
		boolean isNegativ = false;
		if (index < 0) {
			isNegativ = true;
		}
		int abs = Math.abs(index);
		int weight = weights[abs];
		priorityForCenter = BigDecimal.valueOf(1).setScale(6);
		if (!isNegativ) {
			priorityForLeft = BigDecimal.valueOf(weight).setScale(6);
			priorityForRight = BigDecimal.ONE.divide(priorityForLeft, 6, RoundingMode.HALF_EVEN);
		} else {
			priorityForRight = BigDecimal.valueOf(weight).setScale(6);
			priorityForLeft = BigDecimal.ONE.divide(priorityForRight, 6, RoundingMode.HALF_EVEN);
		}
	}

	/**
	 * Gibt die Gewichtung für die linke Alternative
	 * @return Gewichtung für die linke Alternative
	 */
	protected BigDecimal getPriorityForLeft() {
		return priorityForLeft;
	}

	/**
	 * Gibt die Gewichtung für die rechte Alternative
	 * @return Gewichtung für die rechte Alternative
	 */
	protected BigDecimal getPriorityForRight() {
		return priorityForRight;
	}

	/**
	 * Gibt die neutrale Gewichtung.
	 * @return BigDecimal für die neutrale Gewichtung.
	 */
	public BigDecimal getCentralPriority() {
		return priorityForCenter;
	}

}
