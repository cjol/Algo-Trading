package sampleAlgos;

import java.util.Iterator;

import orderBooks.Order;
import orderBooks.OrderBook;

import database.StockHandle;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;

public class Beast implements ITradingAlgorithm {

	@Override
	public void run(MarketView marketView, Options options) {
		Iterator<StockHandle> stocks = marketView.getAllStocks().iterator();
		StockHandle bestStockEver = stocks.next();
		OrderBook book = marketView.getOrderBook(bestStockEver);
		double woot = 4;
		while(!marketView.isFinished()) {
			marketView.tick();
			woot += Math.random()-0.5;
			if(woot < 5) {
				//BUY BUY BUY
				Iterator<? extends Order> iter = book.getAllOffers();
				if(!iter.hasNext()) continue;
				int bd = iter.next().getPrice();
				marketView.buy(bestStockEver, bd, 1);
			} else {
				//SELL SELL SELL
				Iterator<? extends Order> iter = book.getAllBids();
				if(!iter.hasNext()) continue;
				int bd = iter.next().getPrice();
				marketView.sell(bestStockEver, bd, 1);
			}
			
		}
	}
}
