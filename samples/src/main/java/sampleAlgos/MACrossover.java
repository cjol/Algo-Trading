package sampleAlgos;

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
		
		StockHandle stock = marketView.getAllStocks().get(0);
		
		IValued hb = marketView.getOrderBook(stock).getHighestBid();
		IValued lo = marketView.getOrderBook(stock).getLowestOffer();
		
		IValued slowMA = new MovingAverage(new TwoAverage(hb, lo), slowWindow);
		IValued fastMA = new MovingAverage(new TwoAverage(hb, lo), fastWindow);
		
		//State: slowMA = fastMA: 0, slowMA > fastMA: -1, slowMA < fastMA: 1
		//int prevState = 0;
		int currState = 0;
		
		boolean havePosition = false;
		
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
			
			if (slowVal < fastVal) currState = 1;
			else if (slowVal > fastVal) currState = -1;
			else currState = 0;
			
			//Bull market?
			if (currState == 1 && !havePosition) {
				havePosition = true;
				try {
					marketView.buy(stock, (int) lo.getValue(0), 1);
				} catch (TickOutOfRangeException e) {
					e.printStackTrace();
				}
			} else if (currState == -1 && havePosition) {
				havePosition = false;
				try {
					marketView.sell(stock, (int) hb.getValue(0), 1);
				} catch (TickOutOfRangeException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
