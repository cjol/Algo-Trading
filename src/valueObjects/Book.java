package valueObjects;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Book {
	private StockHandle stockHandle;
	private PriorityQueue<OfferBid> bids;
	private PriorityQueue<OfferBid> offers;
	
	public Book(StockHandle stockHandle, Collection<OfferBid> bids, Collection<OfferBid> offers) {
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
		if (bids.size() == 0) {
			//TODO: throw something?
			return null;
		}
		
		return new HighestBid(bids.peek());
	}
	
	public LowestOffer getLowestOffer() {
		if (offers.size() == 0) {
			//TODO: throw something?
			return null;
		}
		
		return new LowestOffer(offers.peek());
	}

}
