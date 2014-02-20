package testHarness.output;

import testHarness.MarketView;
import database.OutputServer;
import testHarness.TickData;
import testHarness.output.result.Result;

/**
 * Abstract class representing an output
 * @author Lawrence Esswood
 *
 */
public abstract class Output {
	
	protected OutputServer outputServer;
	protected MarketView market;
	
	public abstract Result getResult();
	
	/**
	 * Processes raw data from the test.
	 * @param data All data from a single tick.
	 */
	public abstract void evaluateData(TickData data);
	
	/**
	 * 
	 * @param outputServer The output server that write to the database
	 */
	public Output(OutputServer outputServer) {
		this.outputServer = outputServer;
	}
	
	public void attachMarketView(MarketView marketView) {
		market = marketView;
	}
}