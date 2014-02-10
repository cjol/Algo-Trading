package orderBookReconstructor;

import java.sql.Timestamp;

import testHarness.StockHandle;

public class SellOrder extends Order {
	public SellOrder(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		super(stock, timePlaced, price, volume);
	}
}
