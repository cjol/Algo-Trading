package orderBookReconstructor;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;
import Iterators.ProtectedIterator;
import database.StockHandle;
import database.TestDataHandler;

/**
 * Given a list of orders on the marketplace, reconstructs the
 * state of the order book at a certain timestamp, as well as 
 * returns a list of matches that have occurred.
 */
public class OrderBookReconstructor extends OrderBook{
	private TestDataHandler dataHandler;
	
	//Sets of bids and asks at the current timestamp.
	private TreeSet<BuyOrder> stockBids;
	private TreeSet<SellOrder> stockOffers;
	
	private void initialize() {
		stockBids = new TreeSet<>();
		stockOffers = new TreeSet<>();
	}
	
	/**
	 * Creates an order book reconstructor instance.
	 * @param handle The stock the order book is for.
	 * @param marketOrders A time-ordered collection of market orders.
	 */
	public OrderBookReconstructor(Timestamp startTime, StockHandle handle, TestDataHandler dataHandler) {
		super(handle);
		this.currentTime = startTime;
		this.dataHandler = dataHandler;
		initialize();
	}

	/**
	 * Tries to match one order against the market and add them to the matched orders' list
	 * @param order Order to be added to the order book and matched against.
	 * @param matches A collection to which the found matches will be output. For every buyer-seller
	 * match, outputs a Match instance. An Order can be in several Match instances in case the first Match
	 * didn't completely fill it.
	 */
	private void matchOneOrder(Order order, Collection<Match> matches) {		
	
		if (order instanceof BuyOrder) {
			stockBids.add((BuyOrder) order);
		} else if (order instanceof SellOrder) {
			stockOffers.add((SellOrder) order);
		} else {
			throw new AssertionError("Order not an instance of Buy or SellOrder!");
		}
		
		//Will loop until either the offer we got is completely filled (might take
		//several fills against different orders) or we have nothing to fill it against.
		while (!stockBids.isEmpty() && !stockOffers.isEmpty()) {
			
			//Get the highest-priority bid and ask
			BuyOrder buyOrder = stockBids.last();
			SellOrder sellOrder = stockOffers.last();
			
			//Break if we can't match the orders anymore.
			if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) < 0) break;
			
			//We've got a match!
			//Make a trade on the average price: if the bid is greater than the ask, the
			//buyer and the seller will split the difference.
			BigDecimal avgPrice = (buyOrder.getPrice() .add( sellOrder.getPrice() )) . divide(BigDecimal.valueOf(2));
			matches.add(new Match(order.getVolume(), avgPrice));
			
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
	public Iterator<Match> updateTime() {
		//Fast forwards the state of the order book up to the timestamp
		//and returns all the matching orders that occurred during that time.
		if(currentTime.equals(softTime)) return null;
		
		Iterator<? extends Order> marketOrders;
		try {
			marketOrders = dataHandler.getOrders(handle, currentTime, softTime);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		//I think this was missing
		currentTime = softTime;
		
		LinkedList<Match> matches = new LinkedList<>();
		
		//Continue until the required timestamp is reached.
		while(marketOrders.hasNext()) {
			Order currOrder = marketOrders.next();
			//TODO check this still works (it should).
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
	
	//getHighestBid/LowestOffer return value objects that, when poked,
	//will go back to this book and get the best bid/offer from it.
	//So far they don't support looking back into history.
	@Override
	public HighestBid getHighestBid() {
		return new HighestBid(this);
	}

	@Override
	public LowestOffer getLowestOffer() {
		return new LowestOffer(this);
	}
	
	//The remaining methods are not supposed to be implemented by the matcher.
	@Override
	public BuyOrder buy(int volume, BigDecimal price, Timestamp timestamp) {
		throw new NotImplementedException();
	}

	@Override
	public SellOrder sell(int volume, BigDecimal price, Timestamp timestamp) {
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

	@Override
	public Iterator<BuyOrder> getOtherBids() {
		return getAllBids();
	}

	@Override
	public Iterator<SellOrder> getOtherOffers() {
		return getAllOffers();
	}
}
