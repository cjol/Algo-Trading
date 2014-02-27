package valueObjects;

import java.util.Iterator;

import orderBooks.MarketOrderBook;
import orderBooks.SellOrder;

public class LowestOffer implements IValued {
	private MarketOrderBook orderBook;
	
	public LowestOffer(MarketOrderBook orderBook) {
		this.orderBook = orderBook;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Take all offers from the order book since it's the reconstructor and only
		//has market offers.
		if (ticksBack < 0) throw new TickOutOfRangeException();
		Iterator<SellOrder> offers = orderBook.getOffersAtTicksAgo(ticksBack);
		if (offers == null || !offers.hasNext()) throw new TickOutOfRangeException();

		return (double)offers.next().getPrice();
	}
}
