package sampleAlgos;

import database.StockHandle;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import valueObjects.Addition;
import valueObjects.IValued;
import valueObjects.MovingAverage;
import valueObjects.MovingStandardDeviation;
import valueObjects.TickOutOfRangeException;
import valueObjects.TwoAverage;

public class BollingerBands implements ITradingAlgorithm {

	@Override
	public void run(MarketView marketView, Options options) {
		int deviationWindowSize = Integer.parseInt(options.getParam("deviationWindowSize"));
		int maWindowSize = Integer.parseInt(options.getParam("maWindowSize"));
		int deviations = Integer.parseInt(options.getParam("deviations"));
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
		
		IValued midMarket = new TwoAverage(hb, lo);
		
		IValued movingAverage = new MovingAverage(midMarket, maWindowSize);
		IValued movingStDev = new MovingStandardDeviation(movingAverage, deviationWindowSize);
		
		boolean havePosition = false;
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			double topBand;
			double bottomBand;
			double midMarketVal;
			
			try {
				midMarketVal = midMarket.getValue(0);
				double maVal = movingAverage.getValue(0);
				double stdVal = movingStDev.getValue(0);
				
				topBand = maVal + deviations * stdVal;
				bottomBand = maVal - deviations * stdVal;
			} catch (TickOutOfRangeException e) {
				continue;
			}
			
			//If a stock hits the higher band, sell; if it hits the lower band, buy
			if (midMarketVal >= topBand && havePosition) {
				havePosition = true;
				marketView.sell(stock, (int)midMarketVal, 1); 
			} else if (midMarketVal <= bottomBand && !havePosition) {
				havePosition = true;
				marketView.buy(stock, (int)midMarketVal, 1);
			}
		}

	}

}
