package database;

import java.util.Date;

public class DatasetHandle {
	private final int datasetID;
	private final Date startDate;
	
	DatasetHandle(int datasetID, Date startDate) {
		this.datasetID = datasetID;
		this.startDate = startDate;
	}
	 
	protected int getId() {
		return datasetID;
	}

	public Date getStartDate() {
		return startDate;
	}
}
