package database;

import testHarness.StockHandle;

public class SQLStockHandle extends StockHandle {
	private final int datasetID;
	private final String ticker;

	public SQLStockHandle(int datasetID, String ticker) {
		this.datasetID = datasetID;
		this.ticker = ticker;
	}

	protected int getDatasetID() {
		return datasetID;
	}

	protected String getTicker() {
		return ticker;
	}
}
