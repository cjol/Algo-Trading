package sampleAlgos;

import java.math.BigDecimal;
import java.util.Iterator;

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
			woot += Math.random()-0.5;
			if(woot < 5) {
				//BUY BUY BUY
				BigDecimal bd = book.getAllOffers().next().getPrice();
				marketView.buy(bestStockEver, bd, 1);
			} else {
				//SELL SELL SELL
				BigDecimal bd = book.getAllBids().next().getPrice();
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
