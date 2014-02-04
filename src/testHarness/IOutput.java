package testHarness;

import java.io.Serializable;

public interface IOutput {
	public Serializable getOutput();
	public void evaluateData(TickData data);
}
