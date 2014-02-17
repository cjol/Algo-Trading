package sampleAlgos;

import java.math.BigDecimal;
import java.util.Iterator;

import orderBookReconstructor.Order;

import database.StockHandle;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.OrderBook;

public class Beast implements ITradingAlgorithm {

	@Override
	public void run(MarketView marketView) {
		// TODO Auto-generated method stub
		Iterator<StockHandle> stocks = marketView.getAllStocks();
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
				BigDecimal bd = iter.next().getPrice();
				marketView.buy(bestStockEver, bd, 1);
			} else {
				//SELL SELL SELL
				Iterator<? extends Order> iter = book.getAllBids();
				if(!iter.hasNext()) continue;
				BigDecimal bd = iter.next().getPrice();
				marketView.sell(bestStockEver, bd, 1);
			}
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
