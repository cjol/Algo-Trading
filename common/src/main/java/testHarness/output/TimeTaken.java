package testHarness.output;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import database.OutputServer;
import testHarness.TickData;
import testHarness.output.Output;
import testHarness.output.result.Result;


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
		JSONObject resultMap = new JSONObject();
		for (Entry<Timestamp, Long> timetakenDataPoint : timeTakenData.entrySet()) {
			resultMap.put(timetakenDataPoint.getKey().toString(),timetakenDataPoint.getValue().longValue());
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
