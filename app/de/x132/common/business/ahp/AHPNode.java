package de.x132.common.business.ahp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.util.BigReal;

import de.x132.common.business.core.SingleResult;
import de.x132.comparison.models.Comparison;
import de.x132.node.models.Node;
import de.x132.prioritisation.models.Prioritisation;

/**
 * Stellt einen berechneten AHP Knoten dar, dieser kann auf allen Eternknoten
 * angewendet können. Dazu soll bereits beim Erstellen der Referenz von dieser
 * Klasse der Knoten, sowie die gesamte Priorisierung übergeben werden. Die
 * Berechnung der untergeordneten Kriterien wird bereits im Konstruktor
 * durchgeführt.
 * 
 * @author Max Wick
 *
 */
public class AHPNode {

	private static BigReal[] SAATY_RI = new BigReal[] { new BigReal(0), new BigReal(0), new BigReal(0.52),
			new BigReal(0.90), new BigReal(1.11), new BigReal(1.25), new BigReal(1.35), new BigReal(1.4),
			new BigReal(1.45), new BigReal(1.49), new BigReal(1.51), new BigReal(1.54), new BigReal(1.56),
			new BigReal(1.57), new BigReal(1.58), };
	private static BigReal ACCEPTANCE_BARRIER = new BigReal(0.1);

	private List<Node> childList;
	private FieldVector<BigReal> columnSums;
	private FieldVector<BigReal> averageOverRows;
	private Node parent;
	private FieldMatrix<BigReal> weights;
	private FieldMatrix<BigReal> normlized;
	private BigReal lambdaMax;
	private BigReal ci;
	private BigReal cr;
	int n;

	boolean isValid;

	/**
	 * Erstellt einen AHP Knoten mit Berechnung zum übergebenen {@link Node}.
	 * 
	 * @param node für den der AHP Node erstellt werden soll.
	 * @param prioritisation Die gesamte Priorisierung des Netzwerkes, es werden
	 *            notwendigen Vergleiche aus dieser herangezogen.
	 */
	public AHPNode(Node node, Prioritisation prioritisation) {
		this.parent = node;
		List<Comparison> relevantCompares = getRelevantCompares(node, prioritisation);
		childList = generateChildList(relevantCompares);
		// n ist die anzahl der Unterelemente, die innerhalb des AHP Knoten
		// berechnet werden.
		n = childList.size();
		// Erstellen der Evolutionsmatrix
		weights = createEvolutionMatrix(relevantCompares);
		// Berechnen der Sumenspalten.
		columnSums = columnSumVector();
		// Erstellen der normalisierten Matrix.
		normlized = normalizeMatrixBy(weights, columnSums);
		// Berechnung des Eigenvectors
		averageOverRows = averageOverRows(normlized);
		// Berechnen des maximalen Lambdas
		lambdaMax = averageOverRows.dotProduct(columnSums);
		// Berechnung des CI
		if (n > 2) {
			ci = lambdaMax.add(new BigReal(n).negate())
					.divide(new BigReal(n - 1));
			// Berechnung des CR
			cr = ci.divide(SAATY_RI[n - 1]);
			// Konsistenzprüfung
		} else {
			ci = BigReal.ZERO;
			cr = BigReal.ZERO;
		}
		isValid = cr.compareTo(ACCEPTANCE_BARRIER) <= 0;
	}

	/**
	 * Liefert die Liste die für diesen Knoten relevanten Vergleiche..
	 * @param node für den die Liste geliefert werden soll.
	 * @param prioritisation welche Priorisierung soll dabei herangezogen
	 *            werden.
	 * @return Liste der relevanten Vergleiche.
	 */
	private List<Comparison> getRelevantCompares(Node node, Prioritisation prioritisation) {
		return prioritisation.getComparisons().stream().filter(x -> x.getParent().equals(node))
				.collect(Collectors.toList());
	}

	/**
	 * Erstellt eine Liste von Kindsknoten. Dazu werden alle übergebenen
	 * Vergleiche herangezogen und die Knoten sowohl von Rechts als auch links
	 * der Paarweisen Vergleiche herangezogen.
	 * 
	 * @param compares die zum erstellen der Liste herangezogen werden sollen.
	 * @return eine geordenete Liste mit den Knoten.
	 */
	private static List<Node> generateChildList(List<Comparison> compares) {
		List<Node> nodes = new ArrayList<>();
		Map<String, Node> collect = compares.stream().collect(HashMap::new, (m, c) -> {
			m.put(c.getLeftnode().getName(), c.getLeftnode());
			m.put(c.getRightnode().getName(), c.getRightnode());
		}, HashMap::putAll);
		nodes = new ArrayList<>(collect.values());
		nodes.sort(Comparator.comparing(x -> x.getName()));
		return nodes;
	}

	/**
	 * Liefert einen Vektor mit den Durchschnittswerten über jeweilige Zeile.
	 * @param matrix
	 * @return Vektor mit durchschnittswerten.
	 */
	private static FieldVector<BigReal> averageOverRows(FieldMatrix<BigReal> matrix) {
		BigReal[] averageOverRows = new BigReal[matrix.getRowDimension()];
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			BigReal[] row = matrix.getRow(i);
			BigReal sum = BigReal.ZERO;
			for (BigReal value : row) {
				sum = sum.add(value);
			}
			averageOverRows[i] = sum.divide(new BigReal(matrix.getRowDimension()));
		}
		return new ArrayFieldVector<BigReal>(averageOverRows);
	}

	/**
	 * Liefert eine normalisierte Matrix.
	 * @param matrix die normalisiert werden soll.
	 * @param columnSums Summen der dazugehörigen Spalten.
	 * @return eine normalisierte Matrix, bei der die Summe über die Spalte 1
	 *         ergeben.
	 */
	private static FieldMatrix<BigReal> normalizeMatrixBy(FieldMatrix<BigReal> matrix, FieldVector<BigReal> columnSums) {
		FieldMatrix<BigReal> copy = matrix.copy();
		for (int i = 0; i < columnSums.getDimension(); i++) {
			for (int j = 0; j < columnSums.getDimension(); j++) {
				copy.setEntry(j, i, copy.getEntry(j, i).divide(columnSums.getEntry(i)));
			}
		}
		return copy;
	}

	/**
	 * Liefert einen Summenvektor der Gewichte für jeden Kindknoten der Matrix.
	 * @return Vektor mit den Summenwerten über die Spalten der Gewichtsmatrix.
	 */
	private FieldVector<BigReal> columnSumVector() {
		BigReal[] sums = new BigReal[childList.size()];
		for (Node child : childList) {
			int column = childList.indexOf(child);
			BigReal[] myClumn = weights.getColumn(column);
			sums[column] = BigReal.ZERO;
			for (BigReal value : myClumn) {
				sums[column] = sums[column].add(value);
			}
		}
		return new ArrayFieldVector<BigReal>(sums);
	}

	/**
	 * Erstellt eine EvolutionsMatrix anhand der Vergleiche.
	 * @param compares vergleiche die herangezogen werden.
	 * @return EvolutionsMatrix des Knotens.
	 */
	private Array2DRowFieldMatrix<BigReal> createEvolutionMatrix(List<Comparison> compares) {
		BigReal[][] weightMatrix = new BigReal[childList.size()][childList.size()];
		compares.forEach(x -> {
			PriorityHolder priorityHolder = new PriorityHolder(x.getWeight());
			int rowIndex = childList.indexOf(x.getLeftnode());
			int columnIndex = childList.indexOf(x.getRightnode());
			weightMatrix[rowIndex][rowIndex] = new BigReal(priorityHolder.getCentralPriority());
			weightMatrix[columnIndex][columnIndex] = new BigReal(priorityHolder.getCentralPriority());
			weightMatrix[rowIndex][columnIndex] = new BigReal(priorityHolder.getPriorityForLeft());
			weightMatrix[columnIndex][rowIndex] = new BigReal(priorityHolder.getPriorityForRight());
		});
		return (new Array2DRowFieldMatrix<BigReal>(weightMatrix));
	}

	/**
	 * Liefert die Entity, die für die Berechnung des AHP Nodes diente.
	 * @return ParentKnoten.
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Liefert den Konsistenz Index des Knotens.
	 * @return Konsistenz Index.
	 */
	public BigDecimal getCi() {
		return this.ci.bigDecimalValue();
	}

	/**
	 * Liefert den Consistenz Ratio dieses Knotens.
	 * @return Konsistenz Wert des Knotens.
	 */
	public BigDecimal getCr() {
		return this.cr.bigDecimalValue();
	}

	/**
	 * Gibt zurück, ob die Bewertung innerhalb dieses Knotens im Toleranzbereich
	 * liegt.
	 * 
	 * @return True wenn die Konsistenz im Toleranzbereich liegt, False wenn die
	 *         Konsistenz außerhalb des toleranzbereiches liegt.
	 */
	public Boolean isConsistent() {
		return Boolean.valueOf(this.isValid);
	}

	/**
	 * Liefert eine Liste von {@link SingleResult} mit den Werten für die
	 * Kindknoten dieses AHP Knotens.
	 * 
	 * @return Liste mit {@link SingleResult} für die Kindknoten.
	 */
	public List<SingleResult> singleResult() {
		List<SingleResult> singleResult = new ArrayList<SingleResult>();
		for (Node node : childList) {
			singleResult.add(getPriorityFor(node));
		}
		return singleResult;
	}

	/**
	 * Liefert die Priorität für einen Knoten.
	 * 
	 * @param node für den die Priorität geliefert werden soll.
	 * @return Prioritöt als {@link SingleResult} Key Value Pair.
	 */
	public SingleResult getPriorityFor(Node node) {
		SingleResult result = new SingleResult(node.getName(), this.averageOverRows.getEntry(childList.indexOf(node))
				.bigDecimalValue());
		return result;
	}

	/**
	 * Print funktion des Knoten, es wird eine Tabelle als String hergestellt.
	 * Diese Methode dient rein für Debug Zwecke.
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.getProperty("line.separator"));
		buffer.append(printline(this.childList.size()));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(printHeaderLine());
		buffer.append(System.getProperty("line.separator"));
		buffer.append(printline(this.childList.size()));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(parseAsString(weights));
		buffer.append(System.getProperty("line.separator"));
		buffer.append(parseAsString(normlized));
		buffer.append(System.getProperty("line.separator"));
		buffer.append("Spaltensummen");
		buffer.append(parseAsString(this.childList, columnSums));
		buffer.append(System.getProperty("line.separator"));
		buffer.append("Durchschnittswerte");
		buffer.append(parseAsString(this.childList, averageOverRows));
		buffer.append(System.getProperty("line.separator"));
		buffer.append("Lambda:");
		buffer.append(lambdaMax.bigDecimalValue());
		buffer.append(System.getProperty("line.separator"));
		buffer.append("CI:");
		buffer.append(ci.bigDecimalValue());
		buffer.append(System.getProperty("line.separator"));
		buffer.append("CR:");
		buffer.append(cr.bigDecimalValue());
		return buffer.toString();
	}

	/**
	 * Erstellt eine Zeile als Linie für die Trennung innerhalb der Tabelle,
	 * Überkreuzungen werden als plus dargestellt. Die fixe Spaltenbreite ist
	 * 10.
	 * 
	 * @param columnCount Anzahl der Spalten.
	 * @return horizontale Linie.
	 */
	private String printline(int columnCount) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("+----------+");
		for (int i = 0; i < columnCount; i++) {
			buffer.append("----------+");
		}
		return buffer.toString();
	}

	/**
	 * Schreibt die Zeile für Kopfzeile, es werden die Kinderknoten
	 * herangezogen.
	 * 
	 * @return Zeile mit Namen der Kindsknoten.
	 */
	private String printHeaderLine() {
		StringBuffer buffer = new StringBuffer();
		String column = getFirstTenChars(getParent().getName());
		buffer.append(String.format("|%10s|", column));
		childList.forEach(node -> buffer.append(String.format("%10s|", getFirstTenChars(node.getName()))));
		return buffer.toString();
	}

	/**
	 * Liefert die ersten 10 Zeichen eines String, es sei den er ist kürzer,
	 * dann wird er gesamt geliefert.
	 * @param name welche auf Länge überprüft werden soll.
	 * @return die 10 Zeichen des Strings.
	 */
	private String getFirstTenChars(String name) {
		int cutAtPos = name.length() > 9 ? 10 : name.length();
		return name.substring(0, cutAtPos);
	}

	/**
	 * Schreibt die Matrix in den String, als Tabelle. Erste Spalte ist das
	 * jeweilige Kindknoten.
	 * 
	 * @param matrix dessen Werte in den String geschrieben werden sollen.
	 * @return String mit den Werten aus der übergebenen Matrix.
	 */
	private String parseAsString(FieldMatrix<BigReal> matrix) {
		StringBuffer buffer = new StringBuffer();
		for (Node rowNode : childList) {
			int rowIndex = childList.indexOf(rowNode);
			buffer.append(String.format("|%10s|", getFirstTenChars(rowNode.getName())));
			for (Node columNode : childList) {
				int columnIndex = childList.indexOf(columNode);
				String valueToPrint = matrix.getEntry(rowIndex, columnIndex).bigDecimalValue()
						.setScale(6, RoundingMode.HALF_EVEN).toString();
				buffer.append(String.format("%10s|", valueToPrint));
			}
			buffer.append(System.getProperty("line.separator"));
		}
		buffer.append(printline(this.childList.size()));
		return buffer.toString();
	}

	/**
	 * SChreibt einen Vector als Zeile.
	 * 
	 * @param childList Knoten dessen Namen für die Benennung der Spalten
	 *            herangezogen werden soillen.
	 * @param vector Vektor der in die zweite Zeile geschrieben werden soll.
	 * @return einen String mit Kopfzeile und dem Vektor als Inhalt.
	 */
	private String parseAsString(List<Node> childList, FieldVector<BigReal> vector) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(System.getProperty("line.separator"));
		buffer.append(printline(1));
		buffer.append(System.getProperty("line.separator"));
		for (int i = 0; i < childList.size(); i++) {
			buffer.append(String.format("|%10s|", getFirstTenChars(childList.get(i).getName())));
			buffer.append(String
					.format("%10s|", vector.getEntry(i).bigDecimalValue().setScale(6, RoundingMode.HALF_UP)));
			buffer.append(System.getProperty("line.separator"));
		}
		buffer.append(printline(1));
		return buffer.toString();
	}

}
