package sampleAlgos;

import java.util.Iterator;

import orderBooks.Order;
import orderBooks.OrderBook;
import orderBooks.SellOrder;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import database.StockHandle;

public class BuyAndHold implements ITradingAlgorithm {
	//Buys a stock from the market and holds it forever.
	public void run(MarketView marketView, Options options) {
		Iterator<StockHandle> stocks = marketView.getAllStocks().iterator();
		StockHandle stock = stocks.next();
		
		int account = marketView.getAvailableFunds().intValue();
		
		boolean bought = false;
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			if (!bought) {
				SellOrder order = marketView.getOrderBook(stock).getAllOffers().next();
				if (order == null) continue;
				
				int amount = (account / order.getPrice()) - 1;
				marketView.buy(stock, amount, order.getPrice());
				bought = true;
			}
		}
	}
}
