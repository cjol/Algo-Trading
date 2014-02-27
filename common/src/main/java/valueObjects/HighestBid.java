package valueObjects;

import java.util.Iterator;

import orderBooks.BuyOrder;
import orderBooks.MarketOrderBook;

public class HighestBid implements IValued {
	private MarketOrderBook orderBook;
	
	public HighestBid(MarketOrderBook orderBook) {
		this.orderBook = orderBook;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Take all bids from the order book since it's the reconstructor and only
		//has market bids.
		if (ticksBack < 0) throw new TickOutOfRangeException();
		Iterator<BuyOrder> bids = orderBook.getBidsAtTicksAgo(ticksBack);
		if (bids == null || !bids.hasNext()) throw new TickOutOfRangeException();

		return (double)bids.next().getPrice();
	}

}
