package resultFormats;

import testHarness.output.result.Result;

public class ChartFormat implements OutputFormat {
	private Result result;

	public ChartFormat(Result result) {
		this.result = result;
	}
	public void save(String filename) {
		// TODO Auto-generated method stub
		System.out.println("Save chart here...");
	}

	public void display(boolean delayDisplay) {
		// TODO Auto-generated method stub
		System.out.println("Insert chart here...");
	}
	
	public void finishDisplay() { 
		
	}
}
