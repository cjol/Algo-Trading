package testHarness.output;

import java.util.List;

public class SingletonResult extends Result {

	Object data;
	
	public SingletonResult(Object data){
		this.data = data;
	}
	
	public void outputToFile(String location){
		// TODO
	}
	
	public String outputToString(){
		//TODO
		return null;
	}
}
