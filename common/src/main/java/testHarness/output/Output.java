package testHarness.output;

import java.util.List;

import testHarness.MarketView;
import testHarness.TickData;
import testHarness.output.result.Result;
import database.OutputServer;

/**
 * Abstract class representing an output
 * @author Lawrence Esswood
 *
 */
public abstract class Output {
	
	protected OutputServer outputServer;
	protected MarketView market;
	
	public abstract Result getResult();
	
	public Class<?>[] dependencies() {return null;}
	
	public void deriveResults(List<Output> outputs){
		// by default does nothing since the output is not derived
	}
	
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
	
	public String getSlug() {
		return this.getClass().getCanonicalName();
	}
}
