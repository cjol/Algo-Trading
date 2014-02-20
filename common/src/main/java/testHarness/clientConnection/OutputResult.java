package testHarness.clientConnection;

import java.io.Serializable;

import testHarness.output.Output;

/**
 * The results of a requested output
 * @author Lawrence Esswood
 *
 */
public class OutputResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final Serializable value;
	public final String name;
	
	/**
	 * 
	 * @param value The result of the output
	 * @param name The class name of the output object
	 */
	public OutputResult(Serializable value, String name) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * 
	 * @param fromOutput creates the class from a completed output.
	 */
	public OutputResult(Output fromOutput) {
		this.name = fromOutput.getClass().getName();
		this.value = fromOutput.getOutput();
	}
}