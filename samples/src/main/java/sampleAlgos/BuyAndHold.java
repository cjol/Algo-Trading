package sampleAlgos;

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
		
		int account = marketView.getAvailableFunds().intValue();
		
		boolean bought = false;
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			if (!bought) {
				SellOrder order = marketView.getOrderBook(stock).getAllOffers().next();
				if (order == null) continue;
				
				//int amount = (account / order.getPrice()) - 1;
				marketView.buy(stock, order.getPrice(), 1);
				bought = true;
			}
		}
	}
}
