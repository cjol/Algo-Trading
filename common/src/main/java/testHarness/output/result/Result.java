package testHarness.output.result;

import java.io.Serializable;

import org.json.JSONObject;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jsonData;
	private String name;

	public String getName() {
		return name;
	}
	
	public Result(String name, JSONObject d) {
		this.name = name;
		this.jsonData = d.toString();
	}
	
	public String asJSON() {
		return jsonData;
	}
//	
//	public abstract void outputToFile(String location);
//	
//	public abstract String outputToString();
	
}
