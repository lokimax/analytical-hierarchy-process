package de.x132.prioritisation.transfer;

import java.util.ArrayList;
import java.util.List;

import de.x132.common.AbstractDTO;
import de.x132.common.business.SolvingMethod;
import de.x132.comparison.transfer.ComparisonDTO;

public class PrioritisationDTO extends AbstractDTO{
	
	private SolvingMethod methode;
	private String name;
	private List<ComparisonDTO> comparisons;

	public PrioritisationDTO(){
		comparisons = new ArrayList<>();
	}
	
	public SolvingMethod getMethode() {
		return methode;
	}

	public void setMethode(SolvingMethod methode) {
		this.methode = methode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ComparisonDTO> getComparisons() {
		return comparisons;
	}

	public void setComparisons(List<ComparisonDTO> comparisons) {
		this.comparisons = comparisons;
	}

	public void add(ComparisonDTO dto) {
		this.comparisons.add(dto);
	}

	public void clearComparisons() {
		this.comparisons.clear();
	}
	
	
}
