package orderBookReconstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import testHarness.StockHandle;

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
	
	//Bids and asks at the current timestamp: a map from the stock ticker to the
	//map from a price level to all the orders at that price level.
	//TODO: a Frankenstein monster made of generics, maybe encapsulate?
	private Map<StockHandle, Map<Integer, PriceLevel<BuyOrder>>> stockBids;
	private Map<StockHandle, Map<Integer, PriceLevel<SellOrder>>> stockOffers;
	
	private void initialize() {
		currentId = 0;
		stockBids = new HashMap<StockHandle, Map<Integer, PriceLevel<BuyOrder>>>();
		stockOffers = new HashMap<StockHandle, Map<Integer, PriceLevel<SellOrder>>>();
	}
	
	public OrderBookReconstructor(Collection<Order> orders) {
		this.orders = new ArrayList<>(orders);
		initialize();
	}
	
	/*
	 * Performs matching of buys and sells for one stock.
	 */
	private void performMatching(StockHandle stock) {
		//Drop out if there are no bids or no offers for the stock.
		if (!stockBids.containsKey(stock) || !stockOffers.containsKey(stock)) return;
		
		LinkedList<PriceLevel<BuyOrder>> bids = new LinkedList<>(stockBids.get(stock).values());
		LinkedList<PriceLevel<SellOrder>> offers = new LinkedList<>(stockOffers.get(stock).values());
		
		//Sort bids in the order of decreasing price.
		Collections.sort(bids, new Comparator<PriceLevel<BuyOrder>>() {
			public int compare(PriceLevel<BuyOrder> o1,
					PriceLevel<BuyOrder> o2) {
				return o2.getPrice() - o1.getPrice();
			}
		});
		
		//Sort offers in the order of increasing price.
		Collections.sort(offers, new Comparator<PriceLevel<SellOrder>>() {
			public int compare(PriceLevel<SellOrder> o1,
					PriceLevel<SellOrder> o2) {
				return o1.getPrice() - o2.getPrice();
			}
		});
		
		while (!bids.isEmpty() && !offers.isEmpty() 
				&& bids.getFirst().getPrice() >= offers.getFirst().getPrice()) {
			//Match highest bids with lowest offers
			PriceLevel<BuyOrder> bidLevel = bids.getFirst();
			PriceLevel<SellOrder> offerLevel = offers.getFirst();
			
			while(!bidLevel.getOrders().isEmpty() && !offerLevel.getOrders().isEmpty()) {
				BuyOrder buyOrder = bidLevel.getOrders().poll();
				SellOrder sellOrder = offerLevel.getOrders().poll();
				
				//TODO: at this point, buyOrder.price is not necessarily equal
				//to sellOrder.price (it's definitely greater, though). Who gets the difference?
				
				if (buyOrder.getVolume() > sellOrder.getVolume()) {
					//Sell order completely filled, buy order partially filled.
					//TODO: notify the seller that his order has been filled if needed.
					BuyOrder newBuyOrder = new BuyOrder(
							buyOrder.getStockHandle(), buyOrder.getPrice(), 
							buyOrder.getVolume() - sellOrder.getVolume(), buyOrder.getTimestamp());
					//Push the remains of the order onto the queue (to be matched again with the next sell order)
					bidLevel.getOrders().add(newBuyOrder);
				} else if (buyOrder.getVolume() < sellOrder.getVolume()) {
					//Buy order completely filled, sell order partially filled.
					//TODO: notify the buyer that his order has been filled if needed.
					SellOrder newSellOrder = new SellOrder(
							sellOrder.getStockHandle(), sellOrder.getPrice(),
							sellOrder.getVolume() - buyOrder.getVolume(), sellOrder.getTimestamp());
					//Push the remains of the order onto the queue (to be matched again with the next buy order)
					offerLevel.getOrders().add(newSellOrder);
				} else {
					//Both orders have equal volume and so have been completely filled!
					//TODO: A glorious day indeed! Notify both parties about the outcome if needed.
				}
			}
			
			//Remove empty price levels both from our sorted view and the order book's map.
			if (bidLevel.getOrders().isEmpty()) {
				bids.removeFirst();
				stockBids.get(stock).remove(bidLevel.getPrice());
			}
			
			if (offerLevel.getOrders().isEmpty()) {
				offers.removeFirst();
				stockOffers.get(stock).remove(offerLevel.getPrice());
			}
		}
	}
	
	public OrderBookReconstructorResult getOrderBookAt(double timestamp) {
		//Fast forwards the state of the order book up to the timestamp
		//and returns the resultant order book.
		
		if (orders.get(currentId).getTimestamp() > timestamp) {
			//We are in front of what the user wants. Revert to the start.
			//TODO: could improve performance by keeping track of all previous
			//requests (timestamp, state of the order book) and reverting to the
			//first timestamp before the required one instead.
			initialize();
		}
		
		//Continue until the required timestamp is reached.
		for (; currentId < orders.size(); currentId++) {
			Order currOrder = orders.get(currentId);
			if (currOrder.getTimestamp() > timestamp) break;
			
			StockHandle ticker = currOrder.getStockHandle();
			
			if (currOrder instanceof BuyOrder) {
				//Add this stock to the order book if we don't have it.
				if (!stockBids.containsKey(ticker)) stockBids.put(ticker, 
						new HashMap<Integer, PriceLevel<BuyOrder>>());
				
				//Add this stock's price level to the order book if we don't have it.
				if (!stockBids.get(ticker).containsKey(currOrder.getPrice())) 
					stockBids.get(ticker).put(currOrder.getPrice(), new PriceLevel<BuyOrder>(currOrder.getPrice()));
				
				stockBids.get(ticker).get(currOrder.getPrice()).getOrders().add((BuyOrder) currOrder);
			} else if (currOrder instanceof SellOrder) {
				//Add this stock to the order book if we don't have it.
				if (!stockOffers.containsKey(ticker)) stockOffers.put(ticker, 
						new HashMap<Integer, PriceLevel<SellOrder>>());
				
				//Add this stock's price level to the order book if we don't have it.
				if (!stockOffers.get(ticker).containsKey(currOrder.getPrice())) 
					stockOffers.get(ticker).put(currOrder.getPrice(), new PriceLevel<SellOrder>(currOrder.getPrice()));
				
				stockOffers.get(ticker).get(currOrder.getPrice()).getOrders().add((SellOrder) currOrder);
			}
			
			//Perform order matching for this stock.
			performMatching(ticker);
		}
		
		//Convert the current state of the book into the required format for output.
		//TODO: Danger, returning a direct reference to the internal state (used for the tests only for now)
		return new OrderBookReconstructorResult(stockBids, stockOffers);
	}
}
