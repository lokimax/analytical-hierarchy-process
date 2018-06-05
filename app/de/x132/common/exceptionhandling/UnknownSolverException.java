package de.x132.common.exceptionhandling;

import de.x132.common.business.SolvingMethod;

public class UnknownSolverException extends Exception {
	
	/**
	 * Framework generated SerialversionUID
	 */
	private static final long serialVersionUID = 4434798575628026759L;
	
	private String message;

	public UnknownSolverException(SolvingMethod methode) {
		this.setMessage("Cannot resolve Methode" + methode.name());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
