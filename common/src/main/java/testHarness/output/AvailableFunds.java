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

public class AvailableFunds extends Output{
	
	private Map<Timestamp, BigDecimal> availableFundsData;
	
	public AvailableFunds(OutputServer outputServer) {
		super(outputServer);	
		availableFundsData = new HashMap<Timestamp, BigDecimal>();
	}

	@Override
	public Result getResult() {
		JSONObject resultMap = new JSONObject();
		for (Entry<Timestamp, BigDecimal> fundDataPoint : availableFundsData.entrySet()) {
			resultMap.put(fundDataPoint.getKey().toString(), fundDataPoint.getValue().doubleValue());
		}
		Result result = new Result(resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		availableFundsData.put(data.currentTime, data.availableFunds.add(data.reservedFunds));	
	}
}

