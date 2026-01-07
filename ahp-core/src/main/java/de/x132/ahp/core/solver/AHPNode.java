package de.x132.ahp.core.solver;

import de.x132.ahp.core.dto.SingleResult;
import de.x132.ahp.core.model.Comparison;
import de.x132.ahp.core.model.Node;
import de.x132.ahp.core.model.Prioritisation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.util.BigReal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a calculated AHP node using the Analytic Hierarchy Process.
 * This class performs the complete AHP calculation including:
 * - Evolution matrix creation
 * - Normalization
 * - Eigenvector calculation
 * - Consistency checking (CR <= 0.1)
 *
 * @author Max Wick
 */
@Slf4j
@Getter
public class AHPNode {

    // Saaty's Random Index for different matrix sizes
    private static final BigReal[] SAATY_RI = new BigReal[]{
            new BigReal(0), new BigReal(0), new BigReal(0.52),
            new BigReal(0.90), new BigReal(1.11), new BigReal(1.25),
            new BigReal(1.35), new BigReal(1.4), new BigReal(1.45),
            new BigReal(1.49), new BigReal(1.51), new BigReal(1.54),
            new BigReal(1.56), new BigReal(1.57), new BigReal(1.58)
    };

    private static final BigReal ACCEPTANCE_BARRIER = new BigReal(0.1);

    private final List<Node> childList;
    private final FieldVector<BigReal> columnSums;
    private final FieldVector<BigReal> averageOverRows;
    private final Node parent;
    private final FieldMatrix<BigReal> weights;
    private final FieldMatrix<BigReal> normalized;
    private final BigReal lambdaMax;
    private final BigReal ci;
    private final BigReal cr;
    private final int n;
    private final boolean isValid;

    /**
     * Creates an AHP node with full calculation for the given node.
     *
     * @param node           the node for which to create the AHP calculation
     * @param prioritisation the complete prioritization containing all comparisons
     */
    public AHPNode(Node node, Prioritisation prioritisation) {
        this.parent = node;
        List<Comparison> relevantCompares = getRelevantComparisons(node, prioritisation);
        this.childList = generateChildList(relevantCompares);
        this.n = childList.size();

        // Create evolution matrix
        this.weights = createEvolutionMatrix(relevantCompares);

        // Calculate column sums
        this.columnSums = columnSumVector();

        // Create normalized matrix
        this.normalized = normalizeMatrixBy(weights, columnSums);

        // Calculate eigenvector (average over rows)
        this.averageOverRows = averageOverRows(normalized);

        // Calculate maximum lambda
        this.lambdaMax = averageOverRows.dotProduct(columnSums);

        // Calculate Consistency Index (CI) and Consistency Ratio (CR)
        if (n > 2) {
            this.ci = lambdaMax.add(new BigReal(n).negate())
                    .divide(new BigReal(n - 1));
            this.cr = ci.divide(SAATY_RI[n - 1]);
        } else {
            this.ci = BigReal.ZERO;
            this.cr = BigReal.ZERO;
        }

        // Check consistency
        this.isValid = cr.compareTo(ACCEPTANCE_BARRIER) <= 0;
    }

    /**
     * Gets the relevant comparisons for a given node.
     *
     * @param node           the parent node
     * @param prioritisation the prioritization containing all comparisons
     * @return list of relevant comparisons for this node
     */
    private List<Comparison> getRelevantComparisons(Node node, Prioritisation prioritisation) {
        return prioritisation.getComparisons().stream()
                .filter(x -> x.getParent().equals(node))
                .collect(Collectors.toList());
    }

    /**
     * Generates a sorted list of child nodes from comparisons.
     *
     * @param compares the comparisons to extract nodes from
     * @return sorted list of child nodes
     */
    private static List<Node> generateChildList(List<Comparison> compares) {
        Map<String, Node> nodeMap = new HashMap<>();
        compares.forEach(c -> {
            nodeMap.put(c.getNodeA().getName(), c.getNodeA());
            nodeMap.put(c.getNodeB().getName(), c.getNodeB());
        });
        List<Node> nodes = new ArrayList<>(nodeMap.values());
        nodes.sort(Comparator.comparing(Node::getName));
        return nodes;
    }

    /**
     * Calculates the average over each row of the matrix.
     *
     * @param matrix the matrix to calculate averages for
     * @return vector with average values
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
        return new ArrayFieldVector<>(averageOverRows);
    }

    /**
     * Normalizes a matrix by dividing each column by its sum.
     *
     * @param matrix     the matrix to normalize
     * @param columnSums the column sums
     * @return normalized matrix where each column sums to 1
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
     * Calculates the column sum vector of the weights matrix.
     *
     * @return vector with column sums
     */
    private FieldVector<BigReal> columnSumVector() {
        BigReal[] sums = new BigReal[childList.size()];
        for (Node child : childList) {
            int column = childList.indexOf(child);
            BigReal[] myColumn = weights.getColumn(column);
            sums[column] = BigReal.ZERO;
            for (BigReal value : myColumn) {
                sums[column] = sums[column].add(value);
            }
        }
        return new ArrayFieldVector<>(sums);
    }

    /**
     * Creates the evolution (pairwise comparison) matrix.
     *
     * @param compares the comparisons to create the matrix from
     * @return the evolution matrix
     */
    private Array2DRowFieldMatrix<BigReal> createEvolutionMatrix(List<Comparison> compares) {
        BigReal[][] weightMatrix = new BigReal[childList.size()][childList.size()];

        // Initialize all elements with 0 first
        for (int i = 0; i < childList.size(); i++) {
            for (int j = 0; j < childList.size(); j++) {
                weightMatrix[i][j] = BigReal.ZERO;
            }
        }

        // Set diagonal to 1
        for (int i = 0; i < childList.size(); i++) {
            weightMatrix[i][i] = BigReal.ONE;
        }

        // Fill in comparison values
        compares.forEach(comparison -> {
            int rowIndex = childList.indexOf(comparison.getNodeA());
            int columnIndex = childList.indexOf(comparison.getNodeB());

            BigDecimal value = comparison.getValue();
            BigDecimal reciprocal = comparison.getReciprocalValue();

            // Set comparison values
            weightMatrix[rowIndex][columnIndex] = new BigReal(value);
            weightMatrix[columnIndex][rowIndex] = new BigReal(reciprocal);
        });

        return new Array2DRowFieldMatrix<>(weightMatrix);
    }

    /**
     * Gets the Consistency Index.
     *
     * @return the CI value
     */
    public BigDecimal getCi() {
        return this.ci.bigDecimalValue();
    }

    /**
     * Gets the Consistency Ratio.
     *
     * @return the CR value
     */
    public BigDecimal getCr() {
        return this.cr.bigDecimalValue();
    }

    /**
     * Checks if the node is consistent (CR <= 0.1).
     *
     * @return true if consistent, false otherwise
     */
    public Boolean isConsistent() {
        return this.isValid;
    }

    /**
     * Gets the calculation results for all child nodes.
     *
     * @return list of single results
     */
    public List<SingleResult> getSingleResults() {
        List<SingleResult> results = new ArrayList<>();
        for (Node node : childList) {
            results.add(getPriorityFor(node));
        }
        return results;
    }

    /**
     * Gets the priority for a specific node.
     *
     * @param node the node to get priority for
     * @return the priority as a SingleResult
     */
    public SingleResult getPriorityFor(Node node) {
        int index = childList.indexOf(node);
        BigDecimal value = this.averageOverRows.getEntry(index).bigDecimalValue();
        return SingleResult.of(node.getName(), value);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(System.lineSeparator());
        buffer.append("AHP Node: ").append(parent.getName());
        buffer.append(System.lineSeparator());
        buffer.append("Lambda Max: ").append(lambdaMax.bigDecimalValue());
        buffer.append(System.lineSeparator());
        buffer.append("CI: ").append(ci.bigDecimalValue());
        buffer.append(System.lineSeparator());
        buffer.append("CR: ").append(cr.bigDecimalValue());
        buffer.append(System.lineSeparator());
        buffer.append("Consistent: ").append(isValid);
        buffer.append(System.lineSeparator());

        buffer.append("Priorities:").append(System.lineSeparator());
        for (SingleResult result : getSingleResults()) {
            buffer.append("  ")
                    .append(result.getNodeName())
                    .append(": ")
                    .append(result.getValue().setScale(6, RoundingMode.HALF_UP))
                    .append(System.lineSeparator());
        }

        return buffer.toString();
    }
}
