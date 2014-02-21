package orderBooks;

import java.sql.Timestamp;
import java.util.Iterator;

import valueObjects.HighestBid;
import valueObjects.LowestOffer;
import database.StockHandle;

/**
 * Abstract class representing an order book.
 * 
 */
public abstract class OrderBook {
	// 100 AD in UNIX timestamp. 
	// Early enough not to have any trades before, but not cause Postgres errors.
	protected static final long MinTimestamp = -59011437600L;
	public final StockHandle handle;
	protected Timestamp softTime = new Timestamp(MinTimestamp);
	protected Timestamp currentTime;
	
	public OrderBook(StockHandle handle) {
		this.handle = handle;
	}
	
	/**
	 * Places a bid order into the order book.
	 * @param volume Amount of securities to buy.
	 * @param price Price at which the order is to be placed.
	 * @param time Time at which the order was placed.
	 * @return A reference to the buy order.
	 */
	public abstract BuyOrder buy(int volume, int price, Timestamp time);
	
	/**
	 * Places an ask (sell) order into the order book.
	 * @param volume Amount of securities to sell.
	 * @param price Price at which the order is to be placed.
	 * @param time Time at which the order was placed.
	 * @return A reference to the sell order.
	 */
	public abstract SellOrder sell(int volume, int price, Timestamp time);
	
	/**
	 * Cancels an existing bid in the book.
	 * @param volume the volume to cancel.
	 * @param price the price at which to cancel.
	 */
	public abstract boolean CancelBuyOrder(int volume, int price);
	
	/**
	 * Cancels an existing sell in the book.
	 * @param volume the volume to cancel.
	 * @param price the price at which to cancel.
	 */
	public abstract boolean CancelSellOrder(int volume, int price);
	
	/**
	 * @return An iterator of all the bids in the order book.
	 */
	public abstract Iterator<BuyOrder> getAllBids();
	
	/**
	 * @return An iterator of all the offers in the order book.
	 */
	public abstract Iterator<SellOrder> getAllOffers();
	
	/**
	 * Fast-forwards the order book to the state at time t.
	 * @param t The timestamp to which the book is to be fast-forwarded.
	 * Must be larger than the current time the book is at.
	 * @return An iterator of all matches that occurred in the book during the fast-forwarding.
	 */
	public abstract Iterator<Match> updateTime();
	
	/**
	 * @return An iterator of the offers placed by the user.
	 */
	
	public void softSetTime(Timestamp t) {
		this.softTime = t;
	}
	
	public abstract Iterator<SellOrder> getMyOffers();
	
	/**
	 * @return An iterator of the bids placed by the user.
	 */
	public abstract Iterator<BuyOrder> getMyBids();
	
	/**
	 * 
	 * @return An iterator of the bids placed by others.
	 */
	public abstract Iterator<BuyOrder> getOtherBids();
	
	/**
	 * 
	 * @return An Iterator of the bids placed by others.
	 */
	public abstract Iterator<SellOrder> getOtherOffers();
	
	//TODO: might not work. We don't keep track of the price history in the book,
	//and value objects need that for calculations.
	/**
	 * @return A value object representing the highest bid in the book.
	 */
	public abstract HighestBid getHighestBid();
	
	/**
	 * @return A value object representing the lowest offer in the book.
	 */
	public abstract LowestOffer getLowestOffer();
}
