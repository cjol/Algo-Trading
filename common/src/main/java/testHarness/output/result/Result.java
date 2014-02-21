package testHarness.output.result;

import java.io.Serializable;

import org.json.JSONObject;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSONObject data;
	private String name;

	public String getName() {
		return name;
	}
	
	public Result(String name, JSONObject d) {
		this.name = name;
		data = d;
	}
	
	public String asJSON() {
		return data.toString();
	}
//	
//	public abstract void outputToFile(String location);
//	
//	public abstract String outputToString();
	
}
