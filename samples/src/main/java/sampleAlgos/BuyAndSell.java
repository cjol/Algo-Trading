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
		String stockName = options.getParam("ticker");
		
		StockHandle stock = null;
		for (StockHandle s: marketView.getAllStocks()) {
			if (s.getTicker().equals(stockName)) {
				stock = s;
				break;
			}
		}
		
		if (stock == null) return;
		
		OrderBook book = null;
		boolean buyOrSell = true;
		final int ticksBetween = 10;
		
		int currTick = 0;
		while (!marketView.isFinished()) {
			currTick++;
			marketView.tick();
			
			if (currTick < ticksBetween) continue;
			currTick = 0;

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
