package testHarness.output;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import testHarness.TickData;
import testHarness.output.result.Result;
import testHarness.output.result.SingletonResult;
import database.OutputServer;

public class AvailableFunds extends Output{
	
	private Map<Timestamp, BigDecimal> availableFundsData;
	
	public AvailableFunds(OutputServer outputServer) {
		super(outputServer);	
		availableFundsData = new HashMap<Timestamp, BigDecimal>();
	}

	@Override
	public Result getResult() {
		Map<String, Result> resultMap = new HashMap<String,Result>();
		for (Entry<Timestamp, BigDecimal> fundDataPoint : availableFundsData.entrySet()) {
			resultMap.put(fundDataPoint.getKey().toString(), 
					new SingletonResult(fundDataPoint.getValue()));
		}
		Result result = new Result(resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		availableFundsData.add(data.getAvailableFunds() + data.getReservedFunds());
		
	}

	

}

