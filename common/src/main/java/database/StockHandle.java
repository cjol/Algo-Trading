package database;

/**
 * Represents a particular security in the database.
 *
 */
public class StockHandle {
	private final int datasetID;
	private final String ticker;

	/** 
	 * Creates a new StockHandle object.
	 * 
	 * @param 	datasetID	internal identifier used by database	
	 * @param	ticker		symbol identifying security
	 */
	public StockHandle(int datasetID, String ticker) {
		this.datasetID = datasetID;
		this.ticker = ticker;
	}

	/**
	 * @return	internal identifier used by database
	 */
	protected int getDatasetID() {
		return datasetID;
	}

	/**
	 * @return	ticker symbol for stock
	 */
	protected String getTicker() {
		return ticker;
	}
}
