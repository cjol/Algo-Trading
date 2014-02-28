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
	private static final String name = "Time Taken per Tick";
	private long lastTime;
	
	public TimeTaken(OutputServer outputServer) {
		super(outputServer);	
		timeTakenData = new HashMap<Timestamp, Long>();
		lastTime = System.nanoTime();
	}

	@Override
	public Result getResult() {
		JSONObject resultMap = new JSONObject();
		for (Entry<Timestamp, Long> timetakenDataPoint : timeTakenData.entrySet()) {
			resultMap.put(timetakenDataPoint.getKey().toString(),timetakenDataPoint.getValue());
		}
		Result result = new Result(getSlug(), name, resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
			long currentTime = System.nanoTime();
			long timeTaken = currentTime - lastTime;
			timeTakenData.put(data.currentTime, timeTaken);
			lastTime = currentTime;
				
	}
}
