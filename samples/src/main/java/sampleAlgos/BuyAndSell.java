package sampleAlgos;

import java.util.Iterator;

import orderBooks.OrderBook;

import database.StockHandle;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;

public class BuyAndSell implements ITradingAlgorithm {

	
	
	@Override
	public void run(MarketView marketView) {
		/*
		 * Buys and sells alternatively 1 volume for each stock on each tick
		 */
		
		
		Iterator<StockHandle> stocks = marketView.getAllStocks();
		StockHandle stock = null;
		OrderBook book = null;
		boolean buyOrSell = true;
		
		
		while (!marketView.isFinished()) {
			marketView.tick();
			
			stocks = marketView.getAllStocks();			
			while(stocks.hasNext()){
				stock = stocks.next();
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
			}
							
			buyOrSell = !buyOrSell;
		}
	}

	

}
