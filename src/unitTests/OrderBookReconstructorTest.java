package unitTests;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.OrderBookReconstructor;
import orderBookReconstructor.Order;
import orderBookReconstructor.OrderBookReconstructorResult;
import orderBookReconstructor.SellOrder;

import org.junit.Test;

public class OrderBookReconstructorTest {

	@Test
	public void testBasicMatching() {
		List<Order> testOrders = new LinkedList<>();
		
		testOrders.add(new BuyOrder("AAPL", 100, 10, 0.0));
		testOrders.add(new SellOrder("AAPL", 100, 10, 0.5));

		OrderBookReconstructor reconstructor = new OrderBookReconstructor(testOrders);
		
		//Fast forward until the first buy offer is in the book.
		//It shouldn't be matched with anything yet.
		OrderBookReconstructorResult result1 = reconstructor.getOrderBookAt(0.1);
		assertTrue(result1.getBidsMap().containsKey("AAPL"));
		assertTrue(result1.getBidsMap().get("AAPL").keySet().contains(100));
		assertTrue(result1.getOffersMap().isEmpty());
		
		//Fast forward until both offers are in the book.
		//They should match together, leaving the order book for AAPL empty.
		OrderBookReconstructorResult result2 = reconstructor.getOrderBookAt(1.0);
		assertTrue(result2.getBidsMap().get("AAPL").isEmpty());
		assertTrue(result2.getOffersMap().get("AAPL").isEmpty());
	}
	
	@Test
	public void testPartialFill() {
		List<Order> testOrders = new LinkedList<>();
		
		testOrders.add(new BuyOrder("AAPL", 100, 5, 0.0));
		testOrders.add(new SellOrder("AAPL", 100, 10, 0.5));
		testOrders.add(new BuyOrder("AAPL", 100, 5, 1.0));

		OrderBookReconstructor reconstructor = new OrderBookReconstructor(testOrders);
		
		//Fast forward until the second order (sell 10) has been partially filled
		//so that there are no buy orders and the only sell order has volume 5.
		OrderBookReconstructorResult result1 = reconstructor.getOrderBookAt(0.7);
		assertTrue(result1.getBidsMap().get("AAPL").isEmpty());
		assertTrue(result1.getOffersMap().get("AAPL").get(100).getOrders().size() == 1);
		assertTrue(result1.getOffersMap().get("AAPL").get(100).getOrders().peek().getVolume() == 5);
		
		//Fast forward until the 5-sell order has been filled against the 5-buy order.
		OrderBookReconstructorResult result2 = reconstructor.getOrderBookAt(1.1);
		assertTrue(result2.getBidsMap().get("AAPL").isEmpty());
		assertTrue(result2.getOffersMap().get("AAPL").isEmpty());
	}
	
	private List<Order> getMultiplePriceLevelsTestData() {
		List<Order> testOrders = new LinkedList<>();
		
		//Book: Buy 15 AAPL at 100
		testOrders.add(new BuyOrder("AAPL", 100, 15, 0.1));
		//Book: Buy 15 AAPL at 100, sell 10 AAPL at 101
		testOrders.add(new SellOrder("AAPL", 101, 10, 0.2));
		//Book: Buy 15 AAPL at 100, sell 5 AAPL at 101 (5 from buy at 102 and sell at 101 were matched)
		testOrders.add(new BuyOrder("AAPL", 102, 5, 0.3));
		//Book: Buy 5 AAPL at 100, sell 5 AAPL at 101 (10 from buy and sell at 100 were matched)
		testOrders.add(new SellOrder("AAPL", 100, 10, 0.4));
		//Book: Buy 5 AAPL at 100, sell 5 AAPL at 101, sell 10 AAPL at 102
		testOrders.add(new SellOrder("AAPL", 102, 10, 0.5));
		//Book: Buy 5 AAPL at 100, buy 10 AAPL at 101, sell 10 AAPL at 102 (5 from buy and sell at 101 were matched)
		testOrders.add(new BuyOrder("AAPL", 101, 15, 0.6));
		//Book: sell 10 AAPL at 102 (sell 15 AAPL at 0 was matched with 5@100 and 10@101)
		testOrders.add(new SellOrder("AAPL", 0, 15, 0.7));
		
		return testOrders;
	}
	
	@Test
	public void testMultiplePriceLevels() {
		//Test that price matching with multiple levels works correctly.
		List<Order> testOrders = getMultiplePriceLevelsTestData();

		OrderBookReconstructor reconstructor = new OrderBookReconstructor(testOrders);
		
		OrderBookReconstructorResult result1 = reconstructor.getOrderBookAt(0.15);
		assertTrue(result1.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 15);
		
		OrderBookReconstructorResult result2 = reconstructor.getOrderBookAt(0.25);
		assertTrue(result2.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 15);
		assertTrue(result2.getOffersMap().get("AAPL").get(101).getOrders().peek().getVolume() == 10);
		
		OrderBookReconstructorResult result3 = reconstructor.getOrderBookAt(0.35);
		assertTrue(result3.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 15);
		assertTrue(result3.getOffersMap().get("AAPL").get(101).getOrders().peek().getVolume() == 5);
		
		OrderBookReconstructorResult result4 = reconstructor.getOrderBookAt(0.45);
		assertTrue(result4.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 5);
		assertTrue(result4.getOffersMap().get("AAPL").get(101).getOrders().peek().getVolume() == 5);
		
		OrderBookReconstructorResult result5 = reconstructor.getOrderBookAt(0.55);
		assertTrue(result5.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 5);
		assertTrue(result5.getOffersMap().get("AAPL").get(101).getOrders().peek().getVolume() == 5);
		assertTrue(result5.getOffersMap().get("AAPL").get(102).getOrders().peek().getVolume() == 10);
		
		OrderBookReconstructorResult result6 = reconstructor.getOrderBookAt(0.65);
		assertTrue(result6.getBidsMap().get("AAPL").get(100).getOrders().peek().getVolume() == 5);
		assertTrue(result6.getBidsMap().get("AAPL").get(101).getOrders().peek().getVolume() == 10);
		assertTrue(result6.getOffersMap().get("AAPL").get(102).getOrders().peek().getVolume() == 10);
		
		OrderBookReconstructorResult result7 = reconstructor.getOrderBookAt(0.75);
		assertTrue(result7.getOffersMap().get("AAPL").get(102).getOrders().peek().getVolume() == 10);
	}
	
	
	
	
}
