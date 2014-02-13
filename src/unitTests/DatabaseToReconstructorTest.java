package unitTests;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.OrderBookReconstructor;
import orderBookReconstructor.SellOrder;

import org.junit.Test;

import database.DatasetHandle;
import database.StockHandle;
import database.TestDataHandler;

public class DatabaseToReconstructorTest {

	@Test
	public void test() throws SQLException {
		TestDataHandler dh = new TestDataHandler();
		DatasetHandle dataset = dh.getDataset("test");
		StockHandle stockHandle = dh.getAllStocks(dataset).next();
		
		//NB: timestamp constructor wants the year to be 1900-based,
		//day to be 1-based and month to be 0-based.
		OrderBookReconstructor obr = new OrderBookReconstructor(
				new Timestamp(114, 0, 1, 0, 0, 0, 0), stockHandle, dh);
		
		Iterator<Match> matches = obr.updateTime(new Timestamp(114, 0, 1, 0, 0, 5, 0));
		
		while (matches.hasNext()) {
			Match m = matches.next();
			
			System.out.println("MATCH: BUY " + m.buyOrder.getVolume() + " at " + m.buyOrder.getPrice()
					+ " AND SELL " + m.sellOrder.getVolume() + " at " + m.sellOrder.getPrice() + 
					" (on " + m.quantity + " items)");
		}
		
		System.out.println("BIDS: ");
		
		Iterator<BuyOrder> buyOrders = obr.getAllBids();
		while (buyOrders.hasNext()) {
			BuyOrder bo = buyOrders.next();
			System.out.println(bo.getVolume() + " @ " + bo.getPrice());
		}
		
		System.out.println("OFFERS: ");
		Iterator<SellOrder> sellOrders = obr.getAllOffers();
		while (sellOrders.hasNext()) {
			SellOrder so = sellOrders.next();
			System.out.println(so.getVolume() + " @ " + so.getPrice());
		}
	}

}
