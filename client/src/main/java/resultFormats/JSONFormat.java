package resultFormats;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import testHarness.output.result.Result;

public class JSONFormat implements OutputFormat {
	Result result;
	public JSONFormat(Result result) {
		this.result = result;
	}
	
	public void display() {
		System.out.println(result.getName() + ": ");
		System.out.println(result.asJSON());
		System.out.println();
	}
	
	public void save(String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println(result.getName() + ": ");
			writer.println(result.asJSON());
			writer.println();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
