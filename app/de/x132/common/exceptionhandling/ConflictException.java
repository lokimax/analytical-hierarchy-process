package de.x132.common.exceptionhandling;

/**
 * Exception wird geworfen wenn ein Fehler passiert.
 * 
 * @author Max Wick
 */
public class ConflictException extends Exception {

	/**
	 * Generated Serial Version ID
	 */
	private static final long serialVersionUID = -6094909975715933204L;
	
	/**
	 * Default Konstruktor.
	 * @param message an den Benutzer.
	 */
	public ConflictException(String message) {
		super(message);
	}
}
