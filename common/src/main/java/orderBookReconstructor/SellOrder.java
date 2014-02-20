package orderBookReconstructor;

import java.sql.Timestamp;

import database.StockHandle;

public class SellOrder extends Order {
	public SellOrder(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		super(stock, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.sellOrderComparitor.compare(this, that);
	}
}
