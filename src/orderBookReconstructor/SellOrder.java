package orderBookReconstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

import testHarness.StockHandle;

public class SellOrder extends Order {
	public SellOrder(StockHandle stock, Timestamp timePlaced, BigDecimal price, int volume) {
		super(stock, timePlaced, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.sellOrderComparitor.compare(this, that);
	}
}
