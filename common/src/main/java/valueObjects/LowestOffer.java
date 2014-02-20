package valueObjects;

import orderBooks.OrderBook;

public class LowestOffer implements IValued {
	private OrderBook orderBook;
	
	public LowestOffer(OrderBook orderBook) {
		this.orderBook = orderBook;
	}
	
	@Override
	//FIXME: so far only gets the current best offer, could fix easily
	//when the OrderBookReconstructor is changed since we would simply
	//take order book snapshots now.
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Take all bids from the order book since it's the reconstructor and only
		//has market bids.
		if (ticksBack > 0 || !orderBook.getAllOffers().hasNext()) throw new TickOutOfRangeException();
		
		return (double)orderBook.getAllOffers().next().getPrice();
	}
}
