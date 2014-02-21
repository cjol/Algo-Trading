package testHarness.output;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import testHarness.TickData;
import testHarness.output.result.Result;
import database.OutputServer;
import database.StockHandle;

public class Portfolio extends Output {
	
	private Map<Timestamp, Map<StockHandle, Integer>> portfolioList;
	
	public Portfolio(OutputServer outputServer) {
		super(outputServer);	
		portfolioList = new HashMap<Timestamp,Map<StockHandle, Integer>>();
	}

	@Override
	public Result getResult() {
		JSONObject portfolioMap = new JSONObject();
		
		for (Entry<Timestamp, Map<StockHandle,Integer>> portfolio : portfolioList.entrySet()) {

			JSONObject portfolioItems = new JSONObject();
			for (Entry<StockHandle,Integer> portfolioItem : portfolio.getValue().entrySet()) {
				portfolioItems.put(portfolioItem.getKey().getTicker(), portfolioItem.getValue());
			}
			portfolioMap.put(portfolio.getKey().toString(), portfolioItems);
		}
		Result result = new Result(portfolioMap);
		if (outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		Map<StockHandle, Integer> combinedPortfolio = new HashMap<StockHandle, Integer>();
		for (Entry<StockHandle, Integer> portfolioItem : data.portfolio.entrySet()) {
			combinedPortfolio.put(portfolioItem.getKey(), portfolioItem.getValue()); 
		}
		for (Entry<StockHandle, Integer> portfolioItem : data.reservedPortfolio.entrySet()) {
			if (combinedPortfolio.containsKey(portfolioItem.getKey())) {
				combinedPortfolio.put(portfolioItem.getKey(), 
						combinedPortfolio.get(portfolioItem.getKey()) + portfolioItem.getValue());
			} else {
				combinedPortfolio.put(portfolioItem.getKey(), portfolioItem.getValue());
			}
		}
		
		
		portfolioList.put(data.currentTime, data.portfolio);
		
	}
}
