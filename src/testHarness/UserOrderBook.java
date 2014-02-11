package testHarness;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;
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
		//TODO 
		Iterator<Match> matches = parent.updateTime(t);
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
			nextGhost = (ghost.hasNext()) ? ghost.next() : null;
			calculateNext();
		}
		
		@Override
		public boolean hasNext() {
			return next == null;
		}

		@Override
		public T next() {
			T hold = next;
			calculateNext();
			return hold;
		}
		
		private void calculateNext() {
			if(!parent.hasNext()) {
				next = null;
				return;
			}
			
			if(nextGhost == null) {
				next = parent.next();
				return;
			}
			
			//Filters out ghosts
			
		}

		@Override
		public void remove() {
			throw new RuntimeException("Operation not supported");
		}
	}
}
