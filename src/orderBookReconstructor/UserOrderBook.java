package orderBookReconstructor;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import testHarness.InterleavingIterator;
import testHarness.OrderBook;
import testHarness.PeekableIterator;
import testHarness.ProtectedIterator;
import testHarness.StockHandle;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

public class UserOrderBook extends OrderBook {

	OrderBook parent;
	
	private final TreeSet<BuyOrder> outstandingBids;
	private TreeSet<SellOrder> outstandingOffers;
	
	private HashMap<BuyOrder, Integer> ghostBids;
	private HashMap<SellOrder, Integer> ghostOffers;
	
	public UserOrderBook(StockHandle handle, OrderBook parent) {
		super(handle);
		this.parent = parent;
		
		outstandingBids = new TreeSet<BuyOrder>();
		outstandingOffers = new TreeSet<SellOrder>();
		
		ghostBids = new HashMap<>();
		ghostOffers = new HashMap<>();
	}

	@Override
	public BuyOrder buy(int volume, int price, Timestamp time) {
		BuyOrder bo = new BuyOrder(handle,time, price, volume);
		outstandingBids.add(bo);
		return bo;
	}

	@Override
	public SellOrder sell(int volume, int price, Timestamp time) {
		SellOrder so = new SellOrder(handle,time, price, volume);
		outstandingOffers.add(so);
		return so;
	}

	@Override
	public Iterator<BuyOrder> getAllBids() {
		Comparator<BuyOrder> comp = Order.buyOrderOnlyComparitor;
		PeekableIterator<BuyOrder> a = new PeekableIterator<>(new GhostingIterator<>(parent.getAllBids(),ghostBids));
		PeekableIterator<BuyOrder> b = new PeekableIterator<>(outstandingBids.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		Comparator<SellOrder> comp = Order.sellOrderOnlyComparitor;
		PeekableIterator<SellOrder> a = new PeekableIterator<>(new GhostingIterator<>(parent.getAllOffers(),ghostOffers));
		PeekableIterator<SellOrder> b = new PeekableIterator<>(outstandingOffers.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}

	@Override
	public Iterator<Match> updateTime(Timestamp t) {
		List<Match> userMatches = new LinkedList<>();
		
		//TODO user matches now
		
		Iterator<Match> matches = parent.updateTime(t);
		
		//TODO get in on all the matches that the market had
		while(matches.hasNext()){
			Match match = matches.next();
			
			if(match.order instanceof BuyOrder) {
				removeGhost(ghostBids, (BuyOrder)match.order, match.quantity);
			} else if(match.order instanceof SellOrder) {
				removeGhost(ghostOffers, (SellOrder)match.order, match.quantity);
			}
		}
	}
	
	private <T> void removeGhost(HashMap<T, Integer> ghost,T offer, int q) {
		if(ghost.containsKey(offer)) {
			int left = ghost.get(offer) - q;
			if(left <= 0)ghost.remove(offer);
			else ghost.put(offer, (Integer)left);
		}
	}
	
	private <T> void addGhost(HashMap<T, Integer> ghost,T offer, int q) {
		int val = ((ghost.containsKey(offer)) ? ghost.get(offer) : 0) + q;
		ghost.put(offer, val);
	}
	
	private Match tryMatch(SellOrder sell, BuyOrder buy) {
		
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		return new ProtectedIterator<>(outstandingOffers.iterator());
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		return new ProtectedIterator<>(outstandingBids.iterator());
	}

	@Override
	public HighestBid getHighestBid() {
		return new HighestBid(outstandingBids.first());
	}

	@Override
	public LowestOffer getLowestOffer() {
		return new LowestOffer(outstandingOffers.first());
	}
	
	private static class GhostingIterator<T extends Order> implements Iterator<T> 
	{
		private final Iterator<T> parent;
		private final HashMap<T, Integer> ghost;
		
		private T next;
		
		public GhostingIterator(Iterator<T> parent, HashMap<T, Integer> ghostSet) {
			this.parent = parent;
			this.ghost = ghostSet;
			next = calculateNext();
		}
		
		@Override
		public boolean hasNext() {
			return next == null;
		}

		@Override
		public T next() {
			T hold = next;
			next = calculateNext();
			return hold;
		}
		
		T calculateNext() {
			while(true) {
				if(!parent.hasNext()) {
					return null;
				}
				T pnext = parent.next();
				if(!ghost.containsKey(pnext)) return pnext;
				
				try {
					T clone = (T)pnext.clone();
					int ghostAmount = ghost.get(pnext);
					if(ghostAmount < pnext.getVolume()) {
						clone.decrementVolume(ghostAmount);
						return clone;
					}
					
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Operation not supported");
		}
	}
}
