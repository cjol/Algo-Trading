package sampleAlgos;

import java.util.Iterator;
import java.util.Map.Entry;

import orderBooks.SellOrder;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import database.StockHandle;

public class BuyAndHold implements ITradingAlgorithm {
	//Buys a stock from the market and holds it forever.
	public void run(MarketView marketView, Options options) {
		String stockName = options.getParam("ticker");
		
		StockHandle stock = null;
		for (StockHandle s: marketView.getAllStocks()) {
			if (s.getTicker().equals(stockName)) {
				stock = s;
				break;
			}
		}
		
		if (stock == null) return;		
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			boolean havePosition = false; 
			
			Iterator<Entry<StockHandle, Integer>> portfolio = marketView.getPortfolio();
			
			while (portfolio.hasNext()) {
				Entry<StockHandle, Integer> entry = portfolio.next();
				if (entry.getKey() == stock) {
					havePosition = true;
				}
			}
			
			if (!havePosition) {
				SellOrder order = marketView.getOrderBook(stock).getAllOffers().next();
				if (order == null) continue;
				
				marketView.buy(stock, order.getPrice(), 1);
			}
		}
	}
}
