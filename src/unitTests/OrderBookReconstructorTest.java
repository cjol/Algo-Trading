package unitTests;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Order;
import orderBookReconstructor.OrderBookReconstructor;
import orderBookReconstructor.SellOrder;

import org.junit.Test;

//FIXME: broken by reworking of the Reconstructor, add tests to check
//both for correct Matches and correct bids and asks output.
public class OrderBookReconstructorTest {
	
	private List<Order> getMultiplePriceLevelsTestData() {
		List<Order> testOrders = new LinkedList<>();
		
		//Book: Buy 15 at 100
		testOrders.add(new BuyOrder(null, new Timestamp(100), 100, 15));
		//Book: Buy 15 at 100, sell 10 at 101
		testOrders.add(new SellOrder(null, new Timestamp(200), 101, 10));
		//Book: Buy 15 at 100, sell 5 at 101 (5 from buy at 102 and sell at 101 were matched)
		testOrders.add(new BuyOrder(null, new Timestamp(300), 102, 5));
		//Book: Buy 5 at 100, sell 5 at 101 (10 from buy and sell at 100 were matched)
		testOrders.add(new SellOrder(null, new Timestamp(400), 100, 10));
		//Book: Buy 5 at 100, sell 5 at 101, sell 10 at 102
		testOrders.add(new SellOrder(null, new Timestamp(500), 102, 10));
		//Book: Buy 5 at 100, buy 10 at 101, sell 10 at 102 (5 from buy and sell at 101 were matched)
		testOrders.add(new BuyOrder(null, new Timestamp(600), 101, 15));
		//Book: sell 10 at 102 (sell 15 at 10 was matched with 5@100 and 10@101)
		testOrders.add(new SellOrder(null, new Timestamp(700), 10, 15));
		
		return testOrders;
	}
	
	/**
	 * Checks if there is an order with a given volume and price in an iterator.
	 */
	private boolean hasVolumePrice(Iterator<? extends Order> orders, int volume, int price) {
		while (orders.hasNext()) {
			Order o = orders.next();
			if (o.getVolume() == volume && o.getPrice() == price) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void testMultiplePriceLevels() {
		//Test that price matching with multiple price levels works correctly.
		//Also tests partial matching.
		List<Order> testOrders = getMultiplePriceLevelsTestData();

		OrderBookReconstructor reconstructor = new OrderBookReconstructor(null, testOrders);
		
		reconstructor.updateTime(new Timestamp(150));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 15, 100));
		
		reconstructor.updateTime(new Timestamp(250));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 15, 100));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 10, 101));
		
		reconstructor.updateTime(new Timestamp(350));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 15, 100));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 5, 101));
		
		reconstructor.updateTime(new Timestamp(450));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 5, 100));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 5, 101));
		
		reconstructor.updateTime(new Timestamp(550));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 5, 100));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 5, 101));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 10, 102));
		
		reconstructor.updateTime(new Timestamp(650));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 5, 100));
		assertTrue(hasVolumePrice(reconstructor.getAllBids(), 10, 101));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 10, 102));
		
		reconstructor.updateTime(new Timestamp(750));
		assertTrue(hasVolumePrice(reconstructor.getAllOffers(), 10, 102));
	}
}
