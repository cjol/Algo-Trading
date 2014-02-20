package database;

import java.sql.Timestamp;

/**
 * Represents a particular test data set in the database.
 *
 */
public class DatasetHandle {
	private final int datasetID;
	private final Timestamp startTime, endTime;
	
	/** 
	 * Creates a new DatasetHandle object.
	 * 
	 * @param 	datasetID	internal identifier used by database	
	 * @param	startDate	earliest order in the data set
	 */
	protected DatasetHandle(int datasetID, Timestamp startTime, Timestamp endTime) {
		this.datasetID = datasetID;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	 
	/**
	 * @return	internal identifier used by database
	 */
	protected int getId() {
		return datasetID;
	}

	/**
	 * 
	 * @return	earliest order snapshot in the data set
	 */
	public Timestamp getStartTime() {
		// Timestamp mutable
		return (Timestamp)startTime.clone();
	}
	
	/**
	 * 
	 * @return	latest order snapshot in the data set
	 */
	public Timestamp getEndTime() {
		// Timestamp mutable
		return (Timestamp)endTime.clone();
	}
}
