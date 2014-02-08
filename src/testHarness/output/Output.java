package testHarness.output;

import java.io.Serializable;

import database.OutputServer;
import testHarness.TickData;

public abstract class Output {
	
	protected OutputServer outputServer;
	
	public abstract Serializable getOutput();
	public abstract void evaluateData(TickData data);
	
	public Output(OutputServer outputServer) {
		this.outputServer = outputServer;
	}
}
