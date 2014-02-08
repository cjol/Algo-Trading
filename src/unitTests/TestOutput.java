package unitTests;

import java.io.Serializable;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;

public class TestOutput extends Output {

	public TestOutput(OutputServer outputServer) {
		super(outputServer);
	}

	int i = 0;
	@Override
	public Serializable getOutput() {
		return new SomeData(i);
	}

	@Override
	public void evaluateData(TickData data) {
		i++;
	}

	private static class SomeData implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		public final int data;
		public SomeData(int data) { this.data = data;}
	}
}
