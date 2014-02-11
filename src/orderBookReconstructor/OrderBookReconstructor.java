package orderBookReconstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import testHarness.OrderBook;
import testHarness.ProtectedIterator;
import testHarness.StockHandle;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

/*
 * Given a list of orders on the marketplace, reconstructs the
 * state of the order book at a certain timestamp. Does not
 * concern itself about making the player special/ghosting his
 * orders so far.
 */
public class OrderBookReconstructor extends OrderBook{
	private List<Order> marketOrders;
	
	//Current ID in the orders' list the reconstructor 
	private int currentId;
	
	//Sets of bids and asks at the current timestamp.
	private TreeSet<BuyOrder> stockBids;
	private TreeSet<SellOrder> stockOffers;
	
	private void initialize() {
		currentId = 0;
		stockBids = new TreeSet<>();
		stockOffers = new TreeSet<>();
	}
	
	public OrderBookReconstructor(StockHandle handle, Collection<Order> marketOrders) {
		super(handle);
		this.marketOrders = new ArrayList<>(marketOrders);
		initialize();
	}
	
	/*
	 * Tries to match one order against the market and add them to the matched orders' list 
	 */
	private void matchOneOrder(Order order, Collection<Match> matches) {
		//Drop out if there are no bids or no offers for the stock.
		if (stockBids.isEmpty() || stockOffers.isEmpty()) return;
		
		//If the order matches, it matches with the highest-priority order
		//in the orders' set assuming that order's price is sufficient.
		BuyOrder buyOrder;
		SellOrder sellOrder;
		
		if (order instanceof BuyOrder) {
			buyOrder = (BuyOrder)order;
			stockBids.add(buyOrder);
			sellOrder = stockOffers.first();
		} else if (order instanceof SellOrder) {
			buyOrder = stockBids.first();
			sellOrder = (SellOrder)order;
			stockOffers.add(sellOrder);
		} else {
			throw new AssertionError("Order not an instance of Buy or SellOrder!");
		}
		
		if (buyOrder.getPrice() > sellOrder.getPrice()) {
			//We've got a match.
			matches.add(new Match(buyOrder, order.getVolume()));
			matches.add(new Match(sellOrder, order.getVolume()));
			
			if (buyOrder.getVolume() > sellOrder.getVolume()) {
				//Sell order completely filled, buy order partially filled.
				buyOrder.decrementVolume(sellOrder.getVolume());
				stockOffers.remove(sellOrder);
			} else if (buyOrder.getVolume() < sellOrder.getVolume()) {
				//Buy order completely filled, sell order partially filled.
				sellOrder.decrementVolume(buyOrder.getVolume());
				stockBids.remove(buyOrder);
			} else {
				//Both orders completely filled, pop them off.
				stockBids.remove(buyOrder);
				stockOffers.remove(sellOrder);
			}
		}
	}
	
	@Override
	public Iterator<Match> updateTime(Timestamp timestamp) {
		//Fast forwards the state of the order book up to the timestamp
		//and returns all the matching orders that occurred during that time.
		
		if (marketOrders.get(currentId).getTimePlaced().compareTo(timestamp) > 0) {
			throw new AssertionError("Only fast forward is supported for now!");
		}
		
		LinkedList<Match> matches = new LinkedList<>();
		
		//Continue until the required timestamp is reached.
		for (; currentId < marketOrders.size(); currentId++) {
			Order currOrder = marketOrders.get(currentId);
			if (currOrder.getTimePlaced().compareTo(timestamp) > 0) break;
			
			//Try to match this order with the market and update the matches' list
			matchOneOrder(currOrder, matches);
		}
		
		return new ProtectedIterator<>(matches.iterator());
	}
	
	@Override
	public Iterator<BuyOrder> getAllBids() {
		return new ProtectedIterator<>(stockBids.descendingIterator());
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		return new ProtectedIterator<>(stockOffers.descendingIterator());
	}
	
	@Override
	public HighestBid getHighestBid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LowestOffer getLowestOffer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//The remaining methods are not supposed to be implemented by the matcher.
	@Override
	public BuyOrder buy(int volume, int price, Timestamp timestamp) {
		throw new NotImplementedException();
	}

	@Override
	public SellOrder sell(int volume, int price, Timestamp timestamp) {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		throw new NotImplementedException();
	}
}
