package testHarness.output;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import orderBooks.OrderBook;

import org.json.JSONObject;

import testHarness.MarketView;
import testHarness.TickData;
import testHarness.output.result.Result;
import valueObjects.TickOutOfRangeException;
import database.OutputServer;
import database.StockHandle;

public class PortfolioValueOutput extends Output{
	
	private Map<Timestamp, Integer> portfolioValueData;
	
	public PortfolioValueOutput(OutputServer outputServer) {
		super(outputServer);	
		portfolioValueData = new HashMap<Timestamp,Integer>();
	}

	@Override
	public Result getResult() {
		JSONObject resultMap = new JSONObject();
		for (Entry<Timestamp, Integer> portfolioValue : portfolioValueData.entrySet()) {
			resultMap.put(portfolioValue.getKey().toString(), portfolioValue.getValue());
		}
		Result result = new Result(resultMap);
		if(outputServer != null) outputServer.store(result);
		return result;
	}
	
	public static Integer getPortfolioValue(TickData td, MarketView market, boolean countReserved) {
		int value = 0;
		// go through actual portfolio
		for (Entry<StockHandle, Integer> item : td.portfolio.entrySet()) {
			StockHandle stock = item.getKey();
			OrderBook ob = market.getOrderBook(stock);
			
			try {
				value += ob.getHighestBid().getValue(0) * item.getValue();
			} catch (TickOutOfRangeException e) {
				// Shouldn't happen if the orderbook is defined at the current time (0 ticksAgo!)
				e.printStackTrace();
			}
		}
		if (countReserved) {

			// go through reserved portfolio
			for (Entry<StockHandle, Integer> item : td.reservedPortfolio.entrySet()) {
				StockHandle stock = item.getKey();
				OrderBook ob = market.getOrderBook(stock);
				
				try {
					value += ob.getHighestBid().getValue(0) * item.getValue();
				} catch (TickOutOfRangeException e) {
					// Shouldn't happen if the orderbook is defined at the current time (0 ticksAgo!)
					e.printStackTrace();
				}
			}
		}
		
		return value;
	}

	@Override
	public void evaluateData(TickData data) {
		portfolioValueData.put(data.currentTime, getPortfolioValue(data, market, true) );
		
	}

	

}

