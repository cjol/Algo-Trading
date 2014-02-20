package testHarness.output;

import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class SingletonResult extends Result {

	Object data;
	
	public SingletonResult(Object data){
		this.data = data;
	}
	
	public void outputToFile(String location){
		PrintWriter out;
		try {
			out = new PrintWriter(location);
			out.println(data);
		} catch (FileNotFoundException e) {
			System.err.println("Not found: " + location);
			e.printStackTrace();
		}
		
	}
	
	public String outputToString(){		
		return data.toString();
	}
}
