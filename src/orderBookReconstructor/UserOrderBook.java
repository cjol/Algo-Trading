package orderBookReconstructor;

import java.sql.Timestamp;
import java.util.Comparator;
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
	
	private TreeSet<BuyOrder> ghostBids;
	private TreeSet<SellOrder> ghostOffers;
	
	public UserOrderBook(StockHandle handle, OrderBook parent) {
		super(handle);
		this.parent = parent;
		
		outstandingBids = new TreeSet<BuyOrder>();
		outstandingOffers = new TreeSet<SellOrder>();
		
		ghostBids = new TreeSet<BuyOrder>();
		ghostOffers = new TreeSet<SellOrder>();
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
		
		Iterator<Match> matches = parent.updateTime(t);
		
		//get in on all the matches that the market had
		while(matches.hasNext()){
			Match match = matches.next();
			if(match.getOrder().)
		}
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
		private final Iterator<T> ghost;
		
		private T next;
		private T nextGhost;
		
		public GhostingIterator(Iterator<T> parent, TreeSet<T> ghostSet) {
			this.parent = parent;
			ghost = ghostSet.iterator();
			try {
				nextGhost = (T) ((ghost.hasNext()) ? ghost.next().clone() : null);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			if(!parent.hasNext()) {
				return null;
			}
			
			if(nextGhost == null) {
				return parent.next();
			}
			
			//removes ghosts
			try {
				T nextFromParent = (T) parent.next().clone();
				while(nextFromParent.getPrice() == nextGhost.getPrice()) {
					int parentVol = nextFromParent.getVolume();
					int ghostVol = nextGhost.getVolume();
					if(parentVol > ghostVol) {
						nextFromParent.decrementVolume(ghostVol);
						if(!ghost.hasNext()) {
							nextGhost = null;
							return nextFromParent;
						}
						nextGhost = (T) ghost.next().clone();
					} else {
						if(!parent.hasNext()) return null;
						nextFromParent = (T) parent.next().clone();
						if(parentVol == ghostVol) {
							if(!ghost.hasNext()) {
								nextGhost = null;
								return nextFromParent;
							}
							nextGhost = (T) ghost.next().clone();
						} else nextGhost.decrementVolume(parentVol);
					}
				}
				
				return nextFromParent;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Operation not supported");
		}
	}
}
