package de.x132.ahp.exception;

public class InconsistentMatrixException extends RuntimeException {
    private final double consistencyRatio;

    public InconsistentMatrixException(String message, double consistencyRatio) {
        super(message);
        this.consistencyRatio = consistencyRatio;
    }

    public double getConsistencyRatio() {
        return consistencyRatio;
    }
}
