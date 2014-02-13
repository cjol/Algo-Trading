package testHarness;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.SellOrder;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;


/**
 * Abstract class representing an order book.
 * 
 */
public abstract class OrderBook {
		
	public final StockHandle handle;
		
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
	public abstract BuyOrder buy(int volume, BigDecimal price, Timestamp time);
	
	/**
	 * Places an ask (sell) order into the order book.
	 * @param volume Amount of securities to sell.
	 * @param price Price at which the order is to be placed.
	 * @param time Time at which the order was placed.
	 * @return A reference to the sell order.
	 */
	public abstract SellOrder sell(int volume, BigDecimal price, Timestamp time);
	
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
	public abstract Iterator<Match> updateTime(Timestamp t);
	
	/**
	 * @return An iterator of the offers placed by the user.
	 */
	public abstract Iterator<SellOrder> getMyOffers();
	
	/**
	 * @return An iterator of the bids placed by the user.
	 */
	public abstract Iterator<BuyOrder> getMyBids();
	
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
