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

public class MACrossoverFast implements ITradingAlgorithm {

	@Override
	public void run(MarketView marketView, Options options) {
		int slowWindow = Integer.parseInt(options.getParam("slowWindow"));
		int fastWindow = Integer.parseInt(options.getParam("fastWindow"));
		String stockName = options.getParam("ticker");
		int leverage = Integer.parseInt(options.getParam("leverage"));
		
		StockHandle stock = null;
		for (StockHandle s: marketView.getAllStocks()) {
			if (s.getTicker().equals(stockName)) {
				stock = s;
				break;
			}
		}
		
		if (stock == null) return;
		
		int windowSize = slowWindow > fastWindow ? slowWindow : fastWindow;
		double[] midMarketValues = new double[windowSize];
		int noValues = 0;
		
		IValued hb = marketView.getOrderBook(stock).getHighestBid();
		IValued lo = marketView.getOrderBook(stock).getLowestOffer();
		
		while (!marketView.isFinished()) {
			marketView.tick();
			double hbVal;
			double loVal;
			double midMarketVal;
			
			try {
				hbVal = hb.getValue(0);
				loVal = lo.getValue(0);
				midMarketVal = (hbVal + loVal) * 0.5;
				
				if (noValues == windowSize) {
					for (int i = 1; i < windowSize; i++) {midMarketValues[i-1] = midMarketValues[i];}
					midMarketValues[windowSize - 1] = midMarketVal;
				} else {
					midMarketValues[noValues] = midMarketVal;
					noValues++;
				}
			} catch(TickOutOfRangeException e) {
				continue;
				//Better luck next time
			}
			
			if (!(noValues == windowSize)) continue;
			
			double slowVal = 0.0;
			double fastVal = 0.0;
			
			for (int i = noValues - 1; i > noValues - slowWindow - 1; i--) {
				slowVal += midMarketValues[i];
			}
			
			for (int i = noValues - 1; i > noValues - fastWindow - 1; i--) {
				fastVal += midMarketValues[i];
			}
			
			slowVal /= slowWindow;
			fastVal /= fastWindow;
			
			boolean havePosition = false; 
			
			Iterator<Entry<StockHandle, Integer>> portfolio = marketView.getPortfolio();
			
			while (portfolio.hasNext()) {
				Entry<StockHandle, Integer> entry = portfolio.next();
				if (entry.getKey() == stock && entry.getValue() > 0) {
					havePosition = true;
				}
			}
			
			//Buy at the highest bid and sell at the lowest offer to
			//minimize the possibility of the market moving against us
			//and our orders not being filled
			if (slowVal < fastVal && !havePosition) {
				marketView.buy(stock, (int)loVal, leverage);
			} else if (slowVal > fastVal && havePosition) {
				marketView.sell(stock, (int)hbVal, leverage);
			}
		}

	}

}
