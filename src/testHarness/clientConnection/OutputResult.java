package testHarness.clientConnection;

import java.io.Serializable;

import testHarness.output.Output;

public class OutputResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final Serializable value;
	public final String name;
	
	public OutputResult(Serializable value, String name) {
		this.name = name;
		this.value = value;
	}
	
	public OutputResult(Output fromOutput) {
		this.name = fromOutput.getClass().getName();
		this.value = fromOutput.getOutput();
	}
}