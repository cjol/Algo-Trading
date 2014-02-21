package testHarness.output.result;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSONObject data;
	
	public Result(JSONObject d) {
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
