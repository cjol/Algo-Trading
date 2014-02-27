package resultFormats;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import testHarness.output.result.Result;

public class JSONFormat implements OutputFormat {
	private Result result;
	private static JSONObject output;
	private static String outputString;
	public JSONFormat(Result result) {
		this.result = result;
	}
	
	/**
	 * Prepares the current result for display (actual display delayed in case
	 * we have many such results we want to display in a single JSON string). 
	 */
	public void display(boolean delayDisplay) {
		if (delayDisplay) {
			output.put(result.getName(), new JSONObject(result.asJSON()));
		} else {
			System.out.println(result.getName() + ": ");
			System.out.println(result.asJSON());
		}
	}
	
	public void finishDisplay() {
		if (output != null) {
			System.out.println(output.toString(2));
			output = null;
		}
	}
	
	public void save(String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println(result.asJSON());
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
