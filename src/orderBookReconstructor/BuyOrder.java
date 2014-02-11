package orderBookReconstructor;

import java.sql.Timestamp;
import java.util.Comparator;

import testHarness.StockHandle;

public class BuyOrder extends Order {
	public BuyOrder(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		super(stock, timePlaced, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.buyOrderComparitor.compare(this, that);
	}
	
}
