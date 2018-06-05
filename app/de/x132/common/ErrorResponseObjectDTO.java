package de.x132.common;

/**
 * DTO Objekt zum versenden von Fehlern.
 * 
 * @author Max Wick
 */
public class ErrorResponseObjectDTO extends AbstractDTO{
	
	/**
	 * Typ des Fehlers
	 */
	private ErrorType type;
	
	/**
	 * Nachricht des Fehlers.
	 */
	private String errorMessage;

	/**
	 * Konstruktor zum Erstellen des DTOs.
	 * @param type des Fehlers.
	 * @param errorMessage Nachricht an den Benutzer.
	 */
	public ErrorResponseObjectDTO(ErrorType type, String errorMessage){
		this.type = type;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ErrorType getType() {
		return type;
	}

	public void setType(ErrorType type) {
		this.type = type;
	}
}
