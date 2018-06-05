package de.x132.common.business.transfer;

import java.util.ArrayList;
import java.util.List;

/**
 * Beinhaltet eine Liste der einzelnen Resultate.
 * 
 * @author Max Wick
 *
 */
public class SolvingResultDTO {

	/**
	 * Lieste der einfachen Resultate.
	 */
	private List<SingleResultDTO> singleResults;
	
	public SolvingResultDTO(){
		this.singleResults = new ArrayList<>();
	}

	/**
	 * Liefert die Liste der Resultate.
	 * @return Liste der Resultate
	 */
	public List<SingleResultDTO> getSingleResults() {
		return singleResults;
	}

	/**
	 * Setzt die Liste der Resultate.
	 * @param singleResults der Einzelnen Resultate.
	 */
	public void setSingleResults(List<SingleResultDTO> singleResults) {
		this.singleResults = singleResults;
	}
	
	/**
	 * Fügt mehrere Resultate hinzu.
	 * @param results
	 */
	public void addAll(List<SingleResultDTO> results){
		this.singleResults.addAll(results);
	}
}
