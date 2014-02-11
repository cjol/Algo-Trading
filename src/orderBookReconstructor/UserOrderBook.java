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
		
		ghostBids = new HashMap<BuyOrder, Integer>();
		ghostOffers = new HashMap<SellOrder, Integer>();
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
		PeekableIterator<BuyOrder> a = new PeekableIterator<>(getGhostedBids());
		PeekableIterator<BuyOrder> b = new PeekableIterator<>(outstandingBids.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		Comparator<SellOrder> comp = Order.sellOrderOnlyComparitor;
		PeekableIterator<SellOrder> a = new PeekableIterator<>(getGhostedOffers());
		PeekableIterator<SellOrder> b = new PeekableIterator<>(outstandingOffers.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}
	
	private Iterator<BuyOrder> getGhostedBids() {
		return new GhostingIterator<>(parent.getAllBids(),ghostBids);
	}
	
	private Iterator<SellOrder> getGhostedOffers() {
		return new GhostingIterator<>(parent.getAllOffers(),ghostOffers);
	}

	@Override
	public Iterator<Match> updateTime(Timestamp t) {
		List<Match> userMatches = new LinkedList<>();
		
		Iterator<Match> matches = parent.updateTime(t);
		
		//for all the market matches we can also have a match
		while(matches.hasNext()){
			Match match = matches.next();
			coverMatch(match.buyOrder, match.quantity, ghostBids, ghostOffers, outstandingOffers, userMatches);
			coverMatch(match.sellOrder, match.quantity, ghostOffers, ghostBids, outstandingBids, userMatches);
		}
		
		//match things the market did not.
		match(ghostBids,parent.getAllBids(),outstandingOffers.iterator(), userMatches);
		match(ghostOffers,parent.getAllOffers(),outstandingBids.iterator(), userMatches);
		
		return userMatches.iterator();
	}
	
	private static <Market extends Order, User extends Order> 
	void coverMatch(Market marketOrder, int q, HashMap<Market, Integer> marketGhost, HashMap<User, Integer> userGhost , TreeSet<User> userOrders, List<Match> userMatches) {
		int exisitingGhosting = (marketGhost.containsKey(marketOrder)) ? marketGhost.get(marketOrder) : 0;
		
		int available = q - exisitingGhosting;
		int userIntercepted = 0;
		
		//buy anything the market did in this tick
		while(available > 0 && !userOrders.isEmpty()) {
			User userOrder = userOrders.first();
			if(canTrade(userOrder, marketOrder)) {
				int userVolume = userOrder.getVolume();
				int tradeVolume = (available > userVolume) ? userVolume : available;
				int price = marketOrder.getPrice() + (marketOrder.getPrice() + userOrder.getPrice()) / 2;
				
				userMatches.add(makeMatch(marketOrder, userOrder, tradeVolume, price));
				available -= tradeVolume;
				userIntercepted += tradeVolume;
				
				if(userVolume == tradeVolume) userOrders.remove(userOrder);
				else userOrder.decrementVolume(tradeVolume);
			}
		}
		
		//update ghosting
		int newGhosting = exisitingGhosting + userIntercepted - q;
		if(newGhosting < 0) newGhosting = 0;
		if(exisitingGhosting != 0 && newGhosting == 0) marketGhost.remove(marketOrder);
		else if(newGhosting != 0) marketGhost.put(marketOrder, (Integer)newGhosting);
	}
	
	private static <Market extends Order, User extends Order> 
	void match(HashMap<Market, Integer> ghost, Iterator<Market> marketIter, Iterator<User> userIter, List<Match> userMatches) {
		if( marketIter.hasNext() && userIter.hasNext()) {
			
			Market marketOrder = marketIter.next();	
			User userOrder = userIter.next();
			
			//break totally, no more matches
			while(canTrade(marketOrder, userOrder)) {
					
				//how much we can buy
				int marketVolume = marketOrder.getVolume() - (ghost.containsKey(marketOrder) ? ghost.get(marketOrder) : 0);
				int userVolume = userOrder.getVolume();
				int tradeVolume = (marketVolume > userVolume) ? userVolume : marketVolume;
				int price = (marketOrder.getPrice() + userOrder.getPrice()) / 2;
					
				//ghost
				addGhost(ghost, marketOrder, tradeVolume);
					
				//create match
				userMatches.add(makeMatch(marketOrder, userOrder, tradeVolume, price));
				
				//update out outstanding
				if(tradeVolume == userVolume) {
					userIter.remove();
					if(!userIter.hasNext()) break;
					userOrder = userIter.next();
				} else {
					userOrder.decrementVolume(tradeVolume);
					if(! marketIter.hasNext()) break;
					marketOrder =  marketIter.next();
				}
			}
		}
	}
	
	//FIXME hacky way of losing track of types due to generics...
	private static boolean canTrade(Order a, Order b) {
		boolean swap = (a instanceof BuyOrder);
		
		BuyOrder buy = (BuyOrder) ((swap) ? a : b);
		SellOrder sell = (SellOrder) ((swap) ? b : a);
		
		return (buy.getPrice() >= sell.getPrice());
	}
	
	private static Match makeMatch(Order a, Order b, int q, int p) {
		boolean swap = (a instanceof BuyOrder);
		
		BuyOrder buy = (BuyOrder) ((swap) ? a : b);
		SellOrder sell = (SellOrder) ((swap) ? b : a);
		
		return new Match(buy, sell, q, p);
	}
	
	private static <T> void removeGhost(HashMap<T, Integer> ghost,T offer, int q) {
		if(ghost.containsKey(offer)) {
			int left = ghost.get(offer) - q;
			if(left <= 0) ghost.remove(offer);
			else ghost.put(offer, (Integer)left);
		}
	}
	
	private static <T> void addGhost(HashMap<T, Integer> ghost,T offer, int q) {
		int val = ((ghost.containsKey(offer)) ? ghost.get(offer) : 0) + q;
		ghost.put(offer, val);
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