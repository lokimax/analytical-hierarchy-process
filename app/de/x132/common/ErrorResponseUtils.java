package de.x132.common;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class ErrorResponseUtils{
	
	/**
	 * Erstellt ein JsonObjekt aus für einen Fehler.
	 * @param errortype typ des Fehlers.
	 * @param message Nachrischt des Fehlers.
	 * @return json objekt mit dme Fehler.
	 */
	public static JsonNode createError(ErrorType errortype, String message){
		ErrorResponseObjectDTO error = new ErrorResponseObjectDTO(errortype, message);
		return Json.toJson(error);
	}
}
