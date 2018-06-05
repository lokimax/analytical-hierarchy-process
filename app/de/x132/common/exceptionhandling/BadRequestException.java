package de.x132.common.exceptionhandling;

public class BadRequestException extends Exception {

	/**
	 * Framework generated SerialversionUID
	 */
	private static final long serialVersionUID = 8606602827899430731L;

	public BadRequestException(String message){
		super(message);
	}
}
