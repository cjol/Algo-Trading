package testHarness;

import java.sql.Timestamp;

public abstract class StockHandle {
	// StockHandle objects should be deliberately kept immutable

	public OrderBook getOrderBookAtTime(Timestamp currentTime) {
		//TODO: probably uses orderBookReconstructor to get
		//the data about the market-derived order book at time t,
		//then formats the data from it as needed and puts it into
		//the OrderBook object.
		
		return null;
	}
	
	public int getAmountOwned() {
		// TODO - Is this possible? MarketView is the only one who knows how much you own
		return 0;
	}
}