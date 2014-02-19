package unitTests;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.OrderBook;
import orderBookReconstructor.OrderBookReconstructor;
import orderBookReconstructor.SellOrder;
import orderBookReconstructor.UserOrderBook;

import org.junit.Test;


import database.DatasetHandle;
import database.StockHandle;
import database.TestDataHandler;

public class DatabaseToReconstructorTest {
	
	private void printAllOrders(Iterator<BuyOrder> buyOrders, Iterator<SellOrder> sellOrders) {
		System.out.println("BIDS: ");
		
		while (buyOrders.hasNext()) {
			BuyOrder bo = buyOrders.next();
			System.out.println(bo.getVolume() + " @ " + bo.getPrice());
		}
		
		System.out.println("OFFERS: ");
		while (sellOrders.hasNext()) {
			SellOrder so = sellOrders.next();
			System.out.println(so.getVolume() + " @ " + so.getPrice());
		}
	}
	
	private void printMatches(Iterator<Match> matches) {
		while (matches.hasNext()) {
			Match m = matches.next();
			
			System.out.println("MATCH: BUY " + m.buyOrder.getVolume() + " at " + m.buyOrder.getPrice()
					+ " AND SELL " + m.sellOrder.getVolume() + " at " + m.sellOrder.getPrice() + 
					" (on " + m.quantity + " items, price " + m.price + ")");
		}		
	}

	@Test
	public void test() throws SQLException {
		TestDataHandler dh = new TestDataHandler();
		DatasetHandle dataset = dh.getDataset("test");
		StockHandle stockHandle = dh.getAllStocks(dataset).next();
		
		//NB: timestamp constructor wants the year to be 1900-based,
		//day to be 1-based and month to be 0-based.
		OrderBookReconstructor obr = new OrderBookReconstructor(
				new Timestamp(114, 0, 1, 0, 0, 0, 0), stockHandle, dh);
		
		UserOrderBook userBook = new UserOrderBook(stockHandle, obr);
		
		userBook.buy(2014, 1, new Timestamp(114, 0, 1, 0, 0, 1, 500));
		userBook.softSetTime(new Timestamp(114, 0, 1, 0, 0, 5, 0));
		Iterator<Match> matches = userBook.updateTime();
		
		printAllOrders(userBook.getAllBids(), userBook.getAllOffers());
		printMatches(matches);
		
	}

}
