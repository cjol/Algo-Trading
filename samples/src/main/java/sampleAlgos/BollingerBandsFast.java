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

public class BollingerBandsFast implements ITradingAlgorithm {

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
		
		double[] midMarketValues = new double[windowSize];
		int noValues = 0;
		
		IValued hb = marketView.getOrderBook(stock).getHighestBid();
		IValued lo = marketView.getOrderBook(stock).getLowestOffer();
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			double midMarketVal;
			double hbVal;
			double loVal;
			
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
			} catch (TickOutOfRangeException e) {
				continue;
			}
			
			if (!(noValues == windowSize)) continue;
			
			double sum = 0.0;
			double sumsq = 0.0;
			
			for (int i = 0; i < noValues; i++) {
				sum += midMarketValues[i];
				sumsq += midMarketValues[i] * midMarketValues[i];
			}
			
			double maVal = sum / noValues;
			double stdVal = Math.sqrt(sumsq / noValues - maVal * maVal);
			
			double topBand = maVal + deviations * stdVal;
			double bottomBand = maVal - deviations * stdVal;
			
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
