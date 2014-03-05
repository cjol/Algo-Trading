package sampleAlgos;

import java.util.Iterator;
import java.util.Map.Entry;

import database.StockHandle;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import valueObjects.IValued;
import valueObjects.MovingAverage;
import valueObjects.TickOutOfRangeException;
import valueObjects.TwoAverage;

public class MACrossover implements ITradingAlgorithm {

	@Override
	public void run(MarketView marketView, Options options) {
		int slowWindow = Integer.parseInt(options.getParam("slowWindow"));
		int fastWindow = Integer.parseInt(options.getParam("fastWindow"));
		String stockName = options.getParam("ticker");
		
		StockHandle stock = null;
		for (StockHandle s: marketView.getAllStocks()) {
			if (s.getTicker().equals(stockName)) {
				stock = s;
				break;
			}
		}
		
		if (stock == null) return;
		
		IValued hb = marketView.getOrderBook(stock).getHighestBid();
		IValued lo = marketView.getOrderBook(stock).getLowestOffer();
		
		IValued slowMA = new MovingAverage(new TwoAverage(hb, lo), slowWindow);
		IValued fastMA = new MovingAverage(new TwoAverage(hb, lo), fastWindow);
		
		//State: slowMA = fastMA: 0, slowMA > fastMA: -1, slowMA < fastMA: 1
		//int prevState = 0;
		int currState = 0;
		
		while (!marketView.isFinished()) {
			marketView.tick();
			double slowVal;
			double fastVal;
			
			try {
				slowVal = slowMA.getValue(0);
				fastVal = fastMA.getValue(0);
			} catch(TickOutOfRangeException e) {
				continue;
				//Better luck next time
			}
			
			boolean havePosition = false; 
			
			Iterator<Entry<StockHandle, Integer>> portfolio = marketView.getPortfolio();
			
			while (portfolio.hasNext()) {
				Entry<StockHandle, Integer> entry = portfolio.next();
				if (entry.getKey() == stock && entry.getValue() > 0) {
					havePosition = true;
				}
			}
			
			//Bull market?
			if (slowVal < fastVal && !havePosition) {
				try {
					marketView.buy(stock, (int) lo.getValue(0), 1);
				} catch (TickOutOfRangeException e) {
					e.printStackTrace();
				}
			} else if (slowVal > fastVal && havePosition) {
				try {
					marketView.sell(stock, (int) hb.getValue(0), 1);
				} catch (TickOutOfRangeException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
