package testHarness.clientConnection;

import java.io.Serializable;
import java.util.List;

import testHarness.output.Result;

/**
 * A communication object between the server and client that describes a test result. 
 * @author Lawrence Esswood
 *
 */
public class TestResultDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final List<Result> outputs;
	
	public final boolean testFinished;
	public final String errorMessage;
	
	/**
	 * A normal response.
	 * @param results A list containing all the results a user request be sent back.
	 */
	public TestResultDescription(List<Result> results) {
		this.outputs = results;
		this.testFinished = true;
		this.errorMessage = null;
	}
	
	/**
	 * An erroneous result
	 * @param errorMessage The reason the test failed.
	 */
	public TestResultDescription(String errorMessage) {
		this.outputs = null;
		this.testFinished = false;
		this.errorMessage = errorMessage;
	}
}
