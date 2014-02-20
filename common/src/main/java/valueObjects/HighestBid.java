package valueObjects;

import orderBookReconstructor.OrderBookReconstructor;

public class HighestBid implements IValued {
	private OrderBookReconstructor orderBook;
	
	public HighestBid(OrderBookReconstructor orderBook) {
		this.orderBook = orderBook;
	}
	
	@Override
	//FIXME: so far only gets the current best bid, could fix easily
	//when the OrderBookReconstructor is changed since we would simply
	//take order book snapshots now.
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Take all bids from the order book since it's the reconstructor and only
		//has market bids.
		if (ticksBack > 0 || !orderBook.getAllBids().hasNext()) throw new TickOutOfRangeException();
		
		return (double)orderBook.getAllBids().next().getPrice();
	}

}
