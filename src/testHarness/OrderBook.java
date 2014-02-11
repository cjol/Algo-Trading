package testHarness;

import java.sql.Timestamp;
import java.util.Iterator;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

/*
 * Abstract class representing an order book.
 * 
 */
public abstract class OrderBook {

	
	/* Delete this unless you need to refer to it
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
	} */
		
	public final StockHandle handle;
		
	public OrderBook(StockHandle handle) {
		this.handle = handle;
	}
	
	public abstract BuyOrder buy(int volume, int price, Timestamp time);
	
	public abstract SellOrder sell(int volume, int price, Timestamp time);
	
	public abstract Iterator<BuyOrder> getAllBids();
	
	public abstract Iterator<SellOrder> getAllOffers();
	
	public abstract Iterator<Match> updateTime(Timestamp t);
	
	public abstract Iterator<SellOrder> getMyOffers();
	
	public abstract Iterator<BuyOrder> getMyBids();
	
	public abstract HighestBid getHighestBid();
	
	public abstract LowestOffer getLowestOffer();
}
