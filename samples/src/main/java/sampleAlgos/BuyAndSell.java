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
		 * Buys and sells one stock with a certain time interval
		 */
		Iterator<StockHandle> stocks = marketView.getAllStocks().iterator();
		stocks.next();
		stocks.next();
		stocks.next();
		StockHandle stock = stocks.next();
		OrderBook book = null;
		boolean buyOrSell = true;
		final int ticksBetween = 100;
		
		int currTick = 0;
		while (!marketView.isFinished()) {
			currTick++;
			if (currTick < ticksBetween) continue;
			currTick = 0;
			
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
