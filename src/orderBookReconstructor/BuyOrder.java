package orderBookReconstructor;

import testHarness.StockHandle;

public class BuyOrder extends Order {
	public BuyOrder(StockHandle stock, int price, int volume, double timestamp) {
		super(stock, price, volume, timestamp);
	}
}
