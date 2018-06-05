package de.x132.common.business.transfer;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zur Darstellung von Unterstützungsgraden der Alternativen von Kindelemente. 
 * @author Max Wick
 *
 */
public class ChildResultDTO {
	
	/**
	 * Beinhaltet für jeden Kindelement einen Fullresult, als wäre das Kindelement ein Startknoten.
	 */
	private List<FullResultDTO> solvingResults;
	
	/**
	 * Erstellt eine neue Klasse des Typs
	 */
	public ChildResultDTO(){
		this.setSolvingResults(new ArrayList<>());
	}
	
	/**
	 * Fügt auf einen Rutsch Dullresults dem Objekt hinzu.
	 * @param results
	 */
	public void addAll(List<FullResultDTO> results){
		this.getSolvingResults().addAll(results);
	}

	/**
	 * Liefert die Priorisierungen des Results.
	 * @return liefert die Priorisierungen für jedes Kindelement.
	 */
	public List<FullResultDTO> getSolvingResults() {
		return solvingResults;
	}

	/**
	 * Setzt die Liste der Kindelemente innerhalb dieses objekts.
	 * @param solvingResults die liste die gesetzt werden soll.
	 */
	public void setSolvingResults(List<FullResultDTO> solvingResults) {
		this.solvingResults = solvingResults;
	}
}
