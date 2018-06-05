package de.x132.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Der Abstrakte Service stellt eine Implementierungs-Schablone für die Services
 * dar. Es deffiniert die Funktionen, die zum Mappen der Objekte von einem DTO
 * in den Entity und umgekehrt zu implementieren sind.
 * 
 * @author Max Wick
 *
 * @param <T> DTO welches vom Service verarbeitet wird.
 * @param <U> Entity welche vom Service verarbeitet werden soll.
 */
public abstract class AbstractService<T extends AbstractDTO, U extends CommonInformation> {

	Class<T> clazz;

	public AbstractService(Class<T> typeParameterClass) {
		this.clazz = typeParameterClass;
	}

	/**
	 * Mappt ein DTO in ein Entity
	 * 
	 * @param source das DTO.
	 * @param target die Entityklasse.
	 */
	public abstract void mapToEntity(T source, U target);

	/**
	 * Mappt eine Entity in ein DTO
	 * 
	 * @param source die Entityklasse
	 * @param target die dTO klasse..
	 */
	public abstract void mapToDto(U source, T target);

	/**
	 * Mappt eine Liste von Entities in eine Liste mit DTOs
	 * 
	 * @param sourceList beinhaltet die Entites die in DTOs gewandelt werden
	 *            müssen.
	 * @return eine Liste mit den DTOs.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public List<T> mapList(List<U> sourceList) throws InstantiationException, IllegalAccessException {
		List<T> targetList = new ArrayList<>();
		for (U source : sourceList) {
			T target = clazz.newInstance();
			mapToDto(source, target);
			targetList.add(target);
		}
		return targetList;
	}
}
