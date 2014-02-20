package testHarness.output;

import java.io.Serializable;

import database.OutputServer;
import testHarness.TickData;

/**
 * Abstract class representing an output
 * @author Lawrence Esswood
 *
 */
public abstract class Output {
	
	protected OutputServer outputServer;
	
	public abstract Serializable getOutput();
	
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
}
