package testHarness;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import valueObjects.HighestBid;
import valueObjects.LowestOffer;

/*
 * User's view of the order book for a particular stock from which they
 * can view bids and offers as well as place their own orders.
 */
public class OrderBook {
	private StockHandle stockHandle;
	private PriorityQueue<OfferBid> bids;
	private PriorityQueue<OfferBid> offers;
	
	/*
	 * -UnaccountedTradeTable
	 * -RawTradeTable
	 * -LastUpdated
	 */
	
	public Trade buy(int volume, int price) {
		return new Trade(stockHandle, volume, price);
		//TODO: allows the user to buy the stock
	}
	
	public Trade sell(int volume, int price) {
		return new Trade(stockHandle, volume, price);
		//TODO: allows the user to sell the stock
	}
	
	public Iterator<Trade> updateTime(double t) {
		//TODO: tries to match user's trades (?)
		/*  get trades from data handler
    		get trades from unaccountedTradeTable (ghost table)
    		perform ghosting
    		check if user flagged (?) trades have happened
    	*/
		
		return null;
	}
	
	public OrderBook(StockHandle stockHandle, Collection<OfferBid> bids, Collection<OfferBid> offers) {
		this.stockHandle = stockHandle;
		
		//Push all bids into the priority queue so that the highest bid has the highest
		//priority (lowest by ordering, since PQ is a min-heap)
		this.bids = new PriorityQueue<>(bids.size(), new Comparator<OfferBid>() {
			@Override
			public int compare(OfferBid o1, OfferBid o2) {
				return o2.getPrice() - o1.getPrice();
			}
		});
		this.bids.addAll(bids);
		
		//Push all asks, this time in reverse order from what we use for bids.
		this.offers = new PriorityQueue<>(bids.size(), new Comparator<OfferBid>() {
			@Override
			public int compare(OfferBid o1, OfferBid o2) {
				return o1.getPrice() - o2.getPrice();
			}
		});
		this.offers.addAll(offers);
	}
	
	public StockHandle getStockHandle() {
		return stockHandle;
	}
	
	public Iterator<OfferBid> getAllBids() {
		//Copy all bids into a list and return its iterator.
		LinkedList<OfferBid> bidsList = new LinkedList<>(bids);
		return bidsList.iterator();
	}
	
	public Iterator<OfferBid> getAllOffers() {
		//Copy all offers into a list and return its iterator.
		LinkedList<OfferBid> offersList = new LinkedList<>(offers);
		return offersList.iterator();
	}
	
	public Iterator<OfferBid> getMyOffers() {
		return null;
		//TODO
	}
	
	public Iterator<OfferBid> getMyBids() {
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
