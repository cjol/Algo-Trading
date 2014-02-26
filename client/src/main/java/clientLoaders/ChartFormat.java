package clientLoaders;

import resultFormats.OutputFormat;
import testHarness.output.result.Result;

public class ChartFormat implements OutputFormat {
	Result result;

	public ChartFormat(Result result) {
		this.result = result;
	}
	
	public void display() {
		// TODO Auto-generated method stub
		System.out.println("Insert chart here...");
	}

	public void save(String filename) {
		// TODO Auto-generated method stub
		System.out.println("Save chart here...");
	}

}
