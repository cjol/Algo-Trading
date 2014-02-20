package testHarness.output;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;

public class timeTaken extends Output{
	
	private List<Long> timeTakenList;
	private Timestamp lastTimestamp;
	
	public timeTaken(OutputServer outputServer) {
		super(outputServer);	
		timeTakenList = new LinkedList<Long>();
		lastTimestamp = null;
	}

	@Override
	public Result getResult() {
		ListDataResult result = new ListDataResult(timeTakenList);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		if(lastTimestamp==null){
			lastTimestamp = data.getDataTimestamp();			
			return;
		}
		else{
			timeTakenList.add(new Long(data.getDataTimestamp().getTime() - lastTimestamp.getTime()));
			lastTimestamp = data.getDataTimestamp();
		}		

		
	}

	

}
