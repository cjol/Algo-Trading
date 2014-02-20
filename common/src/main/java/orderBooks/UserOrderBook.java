package orderBooks;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import valueObjects.HighestBid;
import valueObjects.LowestOffer;
import Iterators.InterleavingIterator;
import Iterators.PeekableIterator;
import Iterators.ProtectedIterator;
import database.StockHandle;

/**
 * A decorating order book that keeps track of user orders and filters another order book with them
 * @author Lawrence Esswood
 *
 */
public class UserOrderBook extends OrderBook {

	OrderBook parent;
	
	private final TreeSet<BuyOrder> outstandingBids;
	private TreeSet<SellOrder> outstandingOffers;
	
	private HashMap<Integer, Integer> ghostBids;
	private HashMap<Integer, Integer> ghostOffers;
	
	public UserOrderBook(StockHandle handle, OrderBook parent) {
		super(handle);
		this.parent = parent;
		this.softTime = parent.softTime;
		this.currentTime = parent.currentTime;
		
		outstandingBids = new TreeSet<BuyOrder>();
		outstandingOffers = new TreeSet<SellOrder>();
		
		ghostBids = new HashMap<Integer, Integer>();
		ghostOffers = new HashMap<Integer, Integer>();
	}

	/**
	 * This method indicates whether or not there are any pending trades for the user.
	 * @return A boolean flag indicating completion.
	 */
	public boolean isComplete() {
		updateTime();
		return outstandingBids.isEmpty() && outstandingOffers.isEmpty();
	}
	
	
	@Override
	public BuyOrder buy(int volume, int price, Timestamp time) {
		updateTime();
		for(BuyOrder bo: outstandingBids) {
			if(bo.getPrice() == price) {
				bo.incrementVolume(volume);
				return bo;
			}
		}
		BuyOrder bo = new BuyOrder(handle,time, price, volume);
		outstandingBids.add(bo);
		return bo;
	}

	@Override
	public SellOrder sell(int volume, int price, Timestamp time) {
		updateTime();
		for(SellOrder so: outstandingOffers) {
			if(so.getPrice() == price) {
				so.incrementVolume(volume);
				return so;
			}
		}
		SellOrder so = new SellOrder(handle,time, price, volume);
		outstandingOffers.add(so);
		return so;
	}

	@Override
	public Iterator<BuyOrder> getAllBids() {
		updateTime();
		Comparator<BuyOrder> comp = Order.buyOrderOnlyComparator;
		PeekableIterator<BuyOrder> a = new PeekableIterator<>(getGhostedBids());
		PeekableIterator<BuyOrder> b = new PeekableIterator<>(outstandingBids.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		updateTime();
		Comparator<SellOrder> comp = Order.sellOrderOnlyComparator;
		PeekableIterator<SellOrder> a = new PeekableIterator<>(getGhostedOffers());
		PeekableIterator<SellOrder> b = new PeekableIterator<>(outstandingOffers.iterator());
		return new InterleavingIterator<>(a, b, comp);
	}
	
	/**
	 * 
	 * @return Bids as the user should see them.
	 */
	private Iterator<BuyOrder> getGhostedBids() {
		updateTime();
		return new GhostingIterator<>(parent.getAllBids(),ghostBids);
	}
	
	/**
	 * 
	 * @return Offers as the user should see them.
	 */
	private Iterator<SellOrder> getGhostedOffers() {
		updateTime();
		return new GhostingIterator<>(parent.getAllOffers(),ghostOffers);
	}

	@Override
	public Iterator<Match> updateTime() {
		//FIXME
		
		if(parent.currentTime.equals(softTime)) return null;
		
		List<Match> userMatches = new LinkedList<>();
		
		Iterator<Match> matches = parent.updateTime();
		
		//for all the market matches we can also have a match
		while(matches.hasNext()){
			Match match = matches.next();
			coverMatch(match.price, match.quantity, ghostBids, outstandingOffers, true, userMatches);
			coverMatch(match.price, match.quantity, ghostOffers, outstandingBids, false, userMatches);
		}
		
		//match things the market did not.
		match(ghostBids,parent.getAllBids(),outstandingOffers.iterator(), userMatches, true);
		match(ghostOffers,parent.getAllOffers(),outstandingBids.iterator(), userMatches, false);
		
		return userMatches.iterator();
	}
	
	@Override
	public void softSetTime(Timestamp t) {
		parent.softSetTime(t);
		super.softSetTime(t);
	}
	
	
	/**
	 * Takes a matched order from the market and allows the user to match with it as well.
	 * @param marketOrder The markets order.
	 * @param q The quantity.
	 * @param marketGhost The ghost map for this type.
	 * @param userOrders The outstanding orders for the user.
	 * @param userMatches The matches list to append matches to.
	 */
	private <User extends Order> 
	void coverMatch(int marketPrice, int q, HashMap<Integer, Integer> marketGhost, TreeSet<User> userOrders, boolean isUserOffer, List<Match> userMatches) {
		//FIXME 
		int exisitingGhosting = (marketGhost.containsKey(marketPrice)) ? marketGhost.get(marketPrice) : 0;
		
		int available = q - exisitingGhosting;
		int userIntercepted = 0;
		
		//buy anything the market did in this tick
		while(available > 0 && !userOrders.isEmpty()) {
			User userOrder = userOrders.first();
			if(canTrade(userOrder.getPrice(), marketPrice, isUserOffer)) {
				int userVolume = userOrder.getVolume();
				int tradeVolume = (available > userVolume) ? userVolume : available;
				
				
				int price = userOrder.getPrice();
				
				userMatches.add(new Match(this.handle, tradeVolume, price, isUserOffer));
				available -= tradeVolume;
				userIntercepted += tradeVolume;
				
				if(userVolume == tradeVolume) userOrders.remove(userOrder);
				else userOrder.decrementVolume(tradeVolume);
			} else break;
		}
		
		//update ghosting
		int newGhosting = exisitingGhosting + userIntercepted - q;
		if(newGhosting < 0) newGhosting = 0;
		if(exisitingGhosting != 0 && newGhosting == 0) marketGhost.remove(marketPrice);
		else if(newGhosting != 0) marketGhost.put((Integer)marketPrice, (Integer)newGhosting);
	}
	
	/**
	 * Matches with the parent book.
	 * @param ghost The ghost map for this type.
	 * @param marketIter An iterator for the markets orders.
	 * @param userIter An iterator for the users orders.
	 * @param userMatches A list to append the output matches to.
	 */
	private <Market extends Order, User extends Order> 
	void match(HashMap<Integer, Integer> ghost, Iterator<Market> marketIter, Iterator<User> userIter, List<Match> userMatches, boolean isUserOrder) {
		//FIXME 
		if( marketIter.hasNext() && userIter.hasNext()) {
			
			Market marketOrder = marketIter.next();	
			User userOrder = userIter.next();
			
			//break totally, no more matches
			while(canTrade(marketOrder.getPrice(), userOrder.getPrice(), isUserOrder)) {
					
				//how much we can buy
				int marketVolume = marketOrder.getVolume() - (ghost.containsKey(marketOrder.getPrice()) ? ghost.get(marketOrder.getPrice()) : 0);
				int userVolume = userOrder.getVolume();
				int tradeVolume = (marketVolume > userVolume) ? userVolume : marketVolume;
				
				//The correct price, the user knows about the marker. The market did not know about the user and would have adjusted its price.
				int price = userOrder.getPrice();
					
				//ghost
				addGhost(ghost, marketOrder.getPrice(), tradeVolume);
					
				//create match
				userMatches.add(new Match(this.handle, tradeVolume, price, isUserOrder));
				
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
	
	private static boolean canTrade(int market, int user, boolean isUserOffer) {
		return ((market == user) || (isUserOffer ^ (market < user)));
	}
	
	/**
	 * Removes ghosting for an order.
	 * @param ghost The ghost map for the given type.
	 * @param priceLevel The order to change.
	 * @param q the amount to decrement by.
	 */
	@SuppressWarnings("unused")
	private static void removeGhost(HashMap<Integer, Integer> ghost,int priceLevel, int q) {
		//FIXME
		
		if(ghost.containsKey(priceLevel)) {
			int left = ghost.get(priceLevel) - q;
			if(left <= 0) ghost.remove(priceLevel);
			else ghost.put(priceLevel, left);
		}
	}
	
	/**
	 * Add ghosting for a type.
	 * @param ghost The ghost map for the given type.
	 * @param priceLevel The order to change.
	 * @param q the amount to increment by.
	 */
	private static void addGhost(HashMap<Integer, Integer> ghost,int priceLevel, int q) {
		//FIXME
		
		int val = ((ghost.containsKey(priceLevel)) ? ghost.get(priceLevel) : 0) + q;
		ghost.put(priceLevel, val);
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		
		updateTime();
		return new ProtectedIterator<>(outstandingOffers.iterator());
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		updateTime();
		return new ProtectedIterator<>(outstandingBids.iterator());
	}

	@Override
	public HighestBid getHighestBid() {
		//TODO: just the market HB or include the user's?
		return parent.getHighestBid();
	}

	@Override
	public LowestOffer getLowestOffer() {
		return parent.getLowestOffer();
	}

	@Override
	public Iterator<BuyOrder> getOtherBids() {
		return getGhostedBids();
	}

	@Override
	public Iterator<SellOrder> getOtherOffers() {
		return getGhostedOffers();
	}
	
	/**
	 * A decorating Iterator that removes ghosts
	 * @author Lawrence Esswood
	 *
	 * @param <T> The type of order
	 */
	private static class GhostingIterator<T extends Order> implements Iterator<T> 
	{
		//FIXME
		private final Iterator<T> parent;
		private final HashMap<Integer, Integer> ghost;
		
		private T next;
		
		/**
		 * 
		 * @param parent The unfiltered Iterator.
		 * @param ghostSet The ghost filter.
		 */
		public GhostingIterator(Iterator<T> parent, HashMap<Integer, Integer> ghostSet) {
			this.parent = parent;
			this.ghost = ghostSet;
			next = calculateNext();
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
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
				int priceLevel = pnext.getPrice();
				
				if(!ghost.containsKey(priceLevel)) return pnext;
				
				try {
					pnext = (T)pnext.clone();
					int ghostAmount = ghost.get(priceLevel);
					if(ghostAmount < pnext.getVolume()) {
						pnext.decrementVolume(ghostAmount);
						return pnext;
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
