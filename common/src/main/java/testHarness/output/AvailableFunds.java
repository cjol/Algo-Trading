package testHarness.output;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import testHarness.TickData;
import testHarness.output.result.Result;
import database.OutputServer;

/**
 * Tracks the funds of the user at each tick
 * @author Christopher Little
 *
 */
public class AvailableFunds extends Output{
	
	private Map<Timestamp, BigDecimal> availableFundsData;
	private static final String slug = "testHarness.output.AvailableFunds";
	private static final String name = "Available Funds";
	
	public AvailableFunds(OutputServer outputServer) {
		super(outputServer);	
		availableFundsData = new HashMap<Timestamp, BigDecimal>();
	}

	@Override
	public Result getResult() {
		// convert our Map to a JSONObject
		JSONObject resultMap = new JSONObject();
		
		for (Entry<Timestamp, BigDecimal> fundDataPoint : availableFundsData.entrySet()) {
			
			// the JSONObject is a dict of TimeStamp:Funds
			resultMap.put(fundDataPoint.getKey().toString(), fundDataPoint.getValue().doubleValue());
		}
		
		// Create result object, save to DB if required, and return
		Result result = new Result(slug, name, resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		// add a new data point
		availableFundsData.put(data.currentTime, data.availableFunds.add(data.reservedFunds));	
	}
}

