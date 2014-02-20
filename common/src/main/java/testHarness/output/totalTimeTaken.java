package testHarness.output;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;

public class totalTimeTaken extends Output{	
	
	private Timestamp firstTimestamp;
	private long totalTimeTaken;
	
	public totalTimeTaken(OutputServer outputServer) {
		super(outputServer);	
		totalTimeTaken = 0;
		firstTimestamp = null;
	}

	@Override
	public Result getResult() {
		SingletonResult result = new SingletonResult(totalTimeTaken);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		if(firstTimestamp==null){
			firstTimestamp = data.getDataTimestamp();			
			return;
		}
		else{
			totalTimeTaken = data.getDataTimestamp().getTime() - firstTimestamp.getTime();			
		}		

		
	}

	

}
