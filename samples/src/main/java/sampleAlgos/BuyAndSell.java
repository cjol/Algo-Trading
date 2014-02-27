package sampleAlgos;

import java.util.Iterator;

import orderBooks.OrderBook;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import database.StockHandle;

public class BuyAndSell implements ITradingAlgorithm {

	
	
	@Override
	public void run(MarketView marketView, Options options) {
		/*
		 * Buys and sells alternatively 1 volume for each stock on each tick
		 */
		Iterator<StockHandle> stocks = marketView.getAllStocks().iterator();
		stocks.next();
		stocks.next();
		stocks.next();
		StockHandle stock = stocks.next();
		OrderBook book = null;
		boolean buyOrSell = true;
		
		while (!marketView.isFinished()) {
			marketView.tick();
			book = marketView.getOrderBook(stock);
			if(buyOrSell){
				if(book.getAllOffers().hasNext()){
					marketView.buy(stock, (int)book.getAllOffers().next().getPrice(), 1);
				}					
			}
			else{
				if(book.getAllBids().hasNext()){
					marketView.sell(stock, (int)book.getAllBids().next().getPrice(), 1);
				}					
			}

			buyOrSell = !buyOrSell;
		}
	}
}
