package testHarness;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

/*
 * User's view of the order book for a particular stock from which they
 * can view bids and offers as well as place their own orders.
 */
public class OrderBook {
	private StockHandle stockHandle;
	private PriorityQueue<BuyOrder> bids;
	private PriorityQueue<SellOrder> offers;
	
	/*
	 * -UnaccountedTradeTable
	 * -RawTradeTable
	 * -LastUpdated
	 */
	
	public BuyOrder buy(int volume, int price) {
		return null;
		//TODO: allows the user to buy the stock
	}
	
	public SellOrder sell(int volume, int price) {
		return null;
		//TODO: allows the user to sell the stock
	}
	
	public Iterator<Order> updateTime(double t) {
		//TODO: tries to match user's trades (?)
		/*  get trades from data handler
    		get trades from unaccountedTradeTable (ghost table)
    		perform ghosting
    		check if user flagged (?) trades have happened
    	*/
		
		return null;
	}
	
	public OrderBook(StockHandle stockHandle, Collection<BuyOrder> bids, Collection<SellOrder> offers) {
		this.stockHandle = stockHandle;
		
		//Push all bids into the priority queue so that the highest bid has the highest
		//priority (lowest by ordering, since PQ is a min-heap)
		this.bids = new PriorityQueue<>(bids.size(), new Comparator<BuyOrder>() {
			@Override
			public int compare(BuyOrder o1, BuyOrder o2) {
				return o2.getPrice() - o1.getPrice();
			}
		});
		this.bids.addAll(bids);
		
		//Push all asks, this time in reverse order from what we use for bids.
		this.offers = new PriorityQueue<>(bids.size(), new Comparator<SellOrder>() {
			@Override
			public int compare(SellOrder o1, SellOrder o2) {
				return o1.getPrice() - o2.getPrice();
			}
		});
		this.offers.addAll(offers);
	}
	
	public StockHandle getStockHandle() {
		return stockHandle;
	}
	
	public Iterator<BuyOrder> getAllBids() {
		//Copy all bids into a list and return its iterator.
		LinkedList<BuyOrder> bidsList = new LinkedList<>(bids);
		return bidsList.iterator();
	}
	
	public Iterator<SellOrder> getAllOffers() {
		//Copy all offers into a list and return its iterator.
		LinkedList<SellOrder> offersList = new LinkedList<>(offers);
		return offersList.iterator();
	}
	
	public Iterator<SellOrder> getMyOffers() {
		return null;
		//TODO
	}
	
	public Iterator<BuyOrder> getMyBids() {
		return null;
		//TODO
	}
	
	public HighestBid getHighestBid() {
		//TODO return the value object representing the highest bid for this stock
		return null;
	}
	
	public LowestOffer getLowestOffer() {
		//TODO return the value object representing the lowest offer for this stock
		return null;
	}
}
