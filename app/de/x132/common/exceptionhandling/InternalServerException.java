package de.x132.common.exceptionhandling;

public class InternalServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9132679294017640468L;

	public InternalServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalServerException(String message) {
		super(message);
	}

	public InternalServerException(Throwable cause) {
		super(cause);
	}

}
