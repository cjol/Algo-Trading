package orderBookReconstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import valueObjects.Book;

/*
 * Given a list of orders on the marketplace, reconstructs the
 * state of the order book at a certain timestamp. Does not
 * concern itself about making the player special/ghosting his
 * orders so far.
 */
public class OrderBookReconstructor {
	private List<Order> orders;
	
	//Current ID in the orders' list the reconstructor 
	private int currentId;
	
	//Bids and asks at the current timestamp, sorted first by price and then by timestamp
	private Map<String, PriorityQueue<BuyOrder>> stockBids;
	private Map<String, PriorityQueue<SellOrder>> stockOffers;
	
	private void initialize() {
		currentId = 0;
		stockBids = new HashMap<String, PriorityQueue<BuyOrder>>();
		stockOffers = new HashMap<String, PriorityQueue<SellOrder>>();
	}
	
	public OrderBookReconstructor(Collection<Order> orders) {
		this.orders = new ArrayList<>(orders);
		initialize();
	}
	
	public Book getOrderBookAt(double timestamp) {
		//Fast forwards the state of the order book up to the timestamp
		//and returns the resultant order book.
		
		if (orders.get(currentId).getTimestamp() > timestamp) {
			//We are in front of what the user wants. Revert to the start.
			initialize();
		}
		
		//Continue until the required timestamp, pushing orders
		//into the relevant queues.
		for (; currentId < orders.size(); currentId++) {
			Order currOrder = orders.get(currentId);
			if (currOrder.getTimestamp() > timestamp) break;
			
			String ticker = currOrder.getTickerSymbol();
			
			if (currOrder instanceof BuyOrder) {
				if (!stockBids.containsKey(ticker)) stockBids.put(ticker, 
						new PriorityQueue<>(orders.size() / 2, new Comparator<BuyOrder>() {
							@Override
							public int compare(BuyOrder o1, BuyOrder o2) {
								//Use the price-timestamp ordering, buy orders with a higher price go first.
								//(have a lower priority number)
								if (o2.getPrice() < o1.getPrice()) return -1;
								else if (o2.getPrice() > o1.getPrice()) return 1;
								else return o2.getTimestamp() < o1.getTimestamp() ? -1 : 1;
							}}));
				
				stockBids.get(ticker).add((BuyOrder) currOrder);
			} else if (currOrder instanceof SellOrder) {
				if (!stockOffers.containsKey(ticker)) stockOffers.put(ticker, 
						new PriorityQueue<>(orders.size() / 2, new Comparator<SellOrder>() {
							@Override
							public int compare(SellOrder o1, SellOrder o2) {
								//Use the price-timestamp ordering, sell orders with a lower price go first
								//(have a lower priority number)
								if (o2.getPrice() < o1.getPrice()) return 1;
								else if (o2.getPrice() > o1.getPrice()) return -1;
								else return o2.getTimestamp() < o1.getTimestamp() ? -1 : 1;
							}}));
				
				stockOffers.get(ticker).add((SellOrder) currOrder);
			}
		}
		
		//Perform matching
		//TODO: does this work? do we get the same results if we match after getting all orders
		//as opposed to doing the matching online, as the orders arrive?
		//Seems that it doesn't, actually: consider these orders:
		//at 10am: buy @12
		//at 11am: sell @12
		//at 12pm: buy @14
		//with online matching, the first two will match
		//with offline matching, 14 will have a greater priority and will match with 12, with
		//the difference of 2 per stock going to whom?
		//but seems to work if we only allow matching at equal prices
		return null;
//		for (String ticker : stockBids.keySet()) if (stockOffers.containsKey(ticker)) {
//			PriorityQueue<BuyOrder> bids = stockBids.get(ticker);
//			PriorityQueue<SellOrder> offers = stockOffers.get(ticker);
//			
//			while (true) {
//				//Problem here: we sometimes may need to skip over the orders
//				//at a price level that we can't match yet.
//			}
//		}
	}
}
