package database;

import java.util.Date;

/**
 * Represents a particular test data set in the database.
 *
 */
public class DatasetHandle {
	private final int datasetID;
	private final Date startDate;
	
	/** 
	 * Creates a new DatasetHandle object.
	 * 
	 * @param 	datasetID	internal identifier used by database	
	 * @param	startDate	earliest order in the data set
	 */
	protected DatasetHandle(int datasetID, Date startDate) {
		this.datasetID = datasetID;
		this.startDate = startDate;
	}
	 
	/**
	 * @return	internal identifier used by database
	 */
	protected int getId() {
		return datasetID;
	}

	/**
	 * 
	 * @return	earliest order in the data set
	 */
	public Date getStartDate() {
		// Date mutable
		return (Date)startDate.clone();
	}
}
