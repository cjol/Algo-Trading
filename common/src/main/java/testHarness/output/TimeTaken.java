package testHarness.output;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;
import testHarness.output.result.Result;
import testHarness.output.result.SingletonResult;

public class TimeTaken extends Output{
	
	private Map<Timestamp, Long> timeTakenData;
	private Timestamp lastTimestamp;
	
	public TimeTaken(OutputServer outputServer) {
		super(outputServer);	
		timeTakenData = new HashMap<Timestamp, Long>();
		lastTimestamp = null;
	}

	@Override
	public Result getResult() {
		Map<String, Result> resultMap = new HashMap<String,Result>();
		for (Entry<Timestamp, Long> timetakenDataPoint : timeTakenData.entrySet()) {
			resultMap.put(timetakenDataPoint.getKey().toString(), 
					new SingletonResult(timetakenDataPoint.getValue()));
		}
		Result result = new Result(resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		if(lastTimestamp==null){
			lastTimestamp = data.currentTime;			
			return;
		}
		else{
			Long timeTaken = new Long(data.currentTime.getTime() - lastTimestamp.getTime());
			timeTakenData.put(data.currentTime, timeTaken);
			lastTimestamp = data.currentTime;
		}			
	}
}
