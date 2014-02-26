package orderBooks;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import valueObjects.HighestBid;
import valueObjects.LowestOffer;
import database.StockHandle;
import database.TestDataHandler;

public class UserBookTest {

	TestUserBook userBook;
	@Before
	public void init() {
		StockHandle h = new StockHandle(0, "foo");
		Timestamp t= new Timestamp(OrderBook.MinTimestamp);
		userBook = new TestUserBook(h, new FakeMarketBook(t, h, null));
		userBook.softSetTime(t);
	}
	
	public <T extends Order> void testSeries(Iterator<T> orders, int[] vols, int[] prices) {
		int i = 0;
		while(orders.hasNext()) {
			Order o = orders.next();
			if(!(o.getPrice() == prices[i] && o.getVolume() == vols[i])) fail();
			i++;
		}
		assert(i == prices.length);
	}
	
	@Test
	public void testBuy() {
		//Buy something
		userBook.buy(10, 60, null);
		
		//Check our order is there
		testSeries(userBook.getMyBids(), new int[]{10}, new int[]{60});
		
		//Check ordering
		testSeries(userBook.getAllBids(), new int[]{10,100,200,300}, new int[]{60,50,40,30});
		
		//Check other bids
		testSeries(userBook.getOtherBids(), new int[]{100,200,300}, new int[]{50,40,30});
		
		//Cancel most of our order
		userBook.CancelBuyOrder(7, 60);
		testSeries(userBook.getMyBids(), new int[]{3}, new int[]{60});
		
		//Cancel the rest of our order
		userBook.CancelBuyOrder(3, 60);
		testSeries(userBook.getMyBids(), new int[]{}, new int[]{});
	}

	@Test
	public void testSell() {
		//Buy something
		userBook.sell(10, 50, null);
		
		//Check our order is there
		testSeries(userBook.getMyOffers(), new int[]{10}, new int[]{50});
		
		//Check ordering
		testSeries(userBook.getAllOffers(), new int[]{10,100,200,300}, new int[]{50,60,70,80});
		
		//Check other bids
		testSeries(userBook.getOtherOffers(), new int[]{100,200,300}, new int[]{60,70,80});
		
		//Cancel most of our order
		userBook.CancelSellOrder(7, 50);
		testSeries(userBook.getMyOffers(), new int[]{3}, new int[]{50});
		
		//Cancel the rest of our order
		userBook.CancelSellOrder(3, 50);
		testSeries(userBook.getMyOffers(), new int[]{}, new int[]{});
	}
	
	@Test
	public void testGhosting() {
		//Added ghosting
		UserOrderBook.addGhost(userBook.ghostBids, 50, 100);
		UserOrderBook.addGhost(userBook.ghostBids, 40, 99);
		UserOrderBook.addGhost(userBook.ghostBids, 30, 300);
		
		//Check it applies
		testSeries(userBook.getOtherBids(), new int[]{101}, new int[]{40});
		
		//Remove ghosting
		UserOrderBook.removeGhost(userBook.ghostBids, 50, 100);
		UserOrderBook.removeGhost(userBook.ghostBids, 40, 99);
		UserOrderBook.removeGhost(userBook.ghostBids, 30, 300);
		
		//Check it has been removed
		testSeries(userBook.getOtherBids(), new int[]{100,200,300}, new int[]{50,40,30});
	}
	
	private static class TestUserBook extends UserOrderBook {

		public TestUserBook(StockHandle handle, OrderBook parent) {
			super(handle, parent);
		}
		
		
	}
	
	private static class FakeMarketBook extends MarketOrderBook {
		public FakeMarketBook(Timestamp startTime, StockHandle handle,
				TestDataHandler dataHandler) {
			super(startTime, handle, dataHandler);
			buyOrders.add(new BuyOrder(handle,50,100));
			buyOrders.add(new BuyOrder(handle,40,200));
			buyOrders.add(new BuyOrder(handle,30,300));
			
			
			sellOrders.add(new SellOrder(handle,60,100));
			sellOrders.add(new SellOrder(handle,70,200));
			sellOrders.add(new SellOrder(handle,80,300));
		}

		List<BuyOrder> buyOrders = new LinkedList<>();
		List<SellOrder> sellOrders = new LinkedList<>();
		
		@Override
		public Iterator<BuyOrder> getAllBids() {
			return buyOrders.iterator();
		}

		@Override
		public Iterator<SellOrder> getAllOffers() {
			return sellOrders.iterator();
		}
		
		@Override
		public Iterator<Match> updateTime() {
			return null;
			
		}
	}
}
