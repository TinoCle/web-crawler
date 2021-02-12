package ar.edu.ubp.das.beans;

public class StatusBean {
	protected Boolean indexed;
	protected String indexDate;
	protected Boolean reindex;
	protected Boolean isUp;

	public Boolean getIndexed() {
		return indexed;
	}

	public String getIndexDate() {
		return indexDate;
	}

	public Boolean getReindex() {
		return reindex;
	}

	public Boolean getIsUp() {
		return isUp;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public void setIndexDate(String indexDate) {
		this.indexDate = indexDate;
	}

	public void setReindex(Boolean reindex) {
		this.reindex = reindex;
	}

	public void setIsUp(Boolean isUp) {
		this.isUp = isUp;
	}
	
	@Override
	public String toString() {
		String output = "\nreindex: " + reindex;
		output += "\nindexed: " + indexed;
		output += "\nisUp: " + isUp;
		return output;
	}
}
