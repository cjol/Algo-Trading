package testHarness.clientConnection;

import java.io.Serializable;
import java.util.List;

public class TestResultDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final List<OutputResult> outputs;
	
	public final boolean testFinished;
	public final String errorMessage;
	
	public TestResultDescription(List<OutputResult> results) {
		this.outputs = results;
		this.testFinished = true;
		this.errorMessage = null;
	}
	
	public TestResultDescription(String errorMessage) {
		this.outputs = null;
		this.testFinished = false;
		this.errorMessage = errorMessage;
	}
}
