package testHarness.output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class ListDataResult extends Result {

	private List data;
	
	public ListDataResult(List data){
		this.data = data;
	}

	/**
	 * Outputs the list with each value on a new line to the specified file 
	 * location (calling toString() on each datum)
	 * @param location desired file location
	 */
	public void outputToFile(String location){
		//TODO This just prints each value on a new line. May like other formats (CSV, etc)
		PrintWriter out;
		try {
			out = new PrintWriter(location);
			for (Object datum : data) {
				out.println(datum.toString());
			}
		} catch (FileNotFoundException e) {
			System.err.println("Not found: " + location);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Outputs the list as a comma-separated string of values (calling toString on each datum)
	 * @return A comma-separated string of values.
	 */
	public String outputToString(){
		String prefix = "";
		String result = "";
		for (Object datum : data) {
			result += prefix;
			result += datum.toString();
			if (prefix.isEmpty()) {
				prefix = ",";
			}
		}
		return result;
	}
	
	
	
}
