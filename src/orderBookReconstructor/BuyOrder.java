package orderBookReconstructor;

import java.sql.Timestamp;

import testHarness.StockHandle;

public class BuyOrder extends Order {
	public BuyOrder(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		super(stock, timePlaced, price, volume);
	}
}
