package sampleAlgos;

import java.util.Iterator;
import java.util.Map.Entry;

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
		int windowSize = Integer.parseInt(options.getParam("windowSize"));
		double deviations = Double.parseDouble(options.getParam("deviations"));
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
		
		IValued hb = marketView.getOrderBook(stock).getHighestBid();
		IValued lo = marketView.getOrderBook(stock).getLowestOffer();
		
		IValued midMarket = new TwoAverage(hb, lo);
		
		IValued movingAverage = new MovingAverage(midMarket, windowSize);
		IValued movingStDev = new MovingStandardDeviation(movingAverage, windowSize);

		while (!marketView.isFinished()) {
			marketView.tick();
			
			double topBand;
			double bottomBand;
			double midMarketVal;
			double hbVal;
			double loVal;
			
			try {
				hbVal = hb.getValue(0);
				loVal = lo.getValue(0);
				midMarketVal = (hbVal + loVal) * 0.5;
				double maVal = movingAverage.getValue(0);
				double stdVal = movingStDev.getValue(0);
				
				topBand = maVal + deviations * stdVal;
				bottomBand = maVal - deviations * stdVal;
			} catch (TickOutOfRangeException e) {
				continue;
			}
			
			boolean havePosition = false; 
			
			Iterator<Entry<StockHandle, Integer>> portfolio = marketView.getPortfolio();
			
			while (portfolio.hasNext()) {
				Entry<StockHandle, Integer> entry = portfolio.next();
				if (entry.getKey() == stock && entry.getValue() > 0) {
					havePosition = true;
				}
			}
			
			//If a stock hits the higher band, sell; if it hits the lower band, buy
			if (midMarketVal >= topBand && havePosition) {
				marketView.sell(stock, (int)hbVal, leverage); 
			} else if (midMarketVal <= bottomBand && !havePosition) {
				marketView.buy(stock, (int)loVal, leverage);
			}
		}

	}

}
