package testHarness.output;

import java.io.Serializable;

public abstract class Result implements Serializable{

	public abstract void outputToFile(String location);
	
	public abstract String outputToString();
	
}
