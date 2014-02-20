package testHarness.output.result;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Result> data;
	
	public Result(Map<String,Result> d) {
		data = d;
	}
	
	public String asJSON() {
		String result;
		if (data.size() > 0) {
			result = "{";
			String prefix = "";
			for (Entry<String,Result> datum : data.entrySet()) {
				result += prefix;
				
				result += "\"";
				result += datum.getKey();
				result += "\"";
				
				result += ": ";
				
				result += datum.getValue().asJSON();
								
				prefix = ", ";
			}
			result += "}";
		} else {
			result = "null";
		}
		return result;
	}
//	
//	public abstract void outputToFile(String location);
//	
//	public abstract String outputToString();
	
}
