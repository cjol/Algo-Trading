package testHarness;

import java.util.Date;

public class StockHandle {
	// StockHandle objects should be deliberately kept immutable

	public OrderBook getOrderBookAtTime(Date currentTime) {
		//TODO: probably uses orderBookReconstructor to get
		//the data about the market-derived order book at time t,
		//then formats the data from it as needed and puts it into
		//the OrderBook object.
		
		return null;
	}
	
	public int getAmountOwned() {
		return 0;
		//TODO
	}
}
