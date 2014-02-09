package orderBookReconstructor;

import testHarness.StockHandle;

public class SellOrder extends Order {
	public SellOrder(StockHandle stock, int price, int volume, double timestamp) {
		super(stock, price, volume, timestamp);
	}
}
