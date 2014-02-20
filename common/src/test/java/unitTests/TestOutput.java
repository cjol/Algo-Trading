package unitTests;

import java.io.Serializable;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;
import testHarness.output.result.Result;

public class TestOutput extends Output {

	public TestOutput(OutputServer outputServer) {
		super(outputServer);
	}

	int i = 0;
	@Override
	public Result getResult() {
		return null;
	}

	@Override
	public void evaluateData(TickData data) {
		i++;
	}

	@SuppressWarnings("unused")
	private static class SomeData implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		public final int data;
		public SomeData(int data) { this.data = data;}
	}
}
