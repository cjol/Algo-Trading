package testHarness.output;

import java.io.Serializable;

public abstract class Result implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public abstract void outputToFile(String location);
	
	public abstract String outputToString();
	
}
