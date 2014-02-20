package testHarness.output.result;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class SingletonResult extends Result {

	Object dataPoint;
	
	public SingletonResult(Object data){
		super(new HashMap<String, Result>());
		this.dataPoint = data;
	}
	
	@Override
	public String asJSON() {
		return dataPoint.toString();
	}
}
