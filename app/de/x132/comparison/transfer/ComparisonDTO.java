package de.x132.comparison.transfer;

import de.x132.common.AbstractDTO;

public class ComparisonDTO extends AbstractDTO{

	private String parentNodeName;
	private String leftNodeName;
	private String rightNodeName;
	private Integer weight;
	
	public String getParentNodeName() {
		return parentNodeName;
	}

	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}

	public String getLeftNodeName() {
		return leftNodeName;
	}

	public void setLeftNodeName(String leftNodeName) {
		this.leftNodeName = leftNodeName;
	}

	public String getRightNodeName() {
		return rightNodeName;
	}

	public void setRightNodeName(String rightNodeName) {
		this.rightNodeName = rightNodeName;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftNodeName == null) ? 0 : leftNodeName.hashCode());
		result = prime * result + ((parentNodeName == null) ? 0 : parentNodeName.hashCode());
		result = prime * result + ((rightNodeName == null) ? 0 : rightNodeName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComparisonDTO other = (ComparisonDTO) obj;
		if (leftNodeName == null) {
			if (other.leftNodeName != null)
				return false;
		} else if (!leftNodeName.equals(other.leftNodeName))
			return false;
		if (parentNodeName == null) {
			if (other.parentNodeName != null)
				return false;
		} else if (!parentNodeName.equals(other.parentNodeName))
			return false;
		if (rightNodeName == null) {
			if (other.rightNodeName != null)
				return false;
		} else if (!rightNodeName.equals(other.rightNodeName))
			return false;
		return true;
	}
}
