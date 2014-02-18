package orderBookReconstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

import database.StockHandle;

public class BuyOrder extends Order {
	public BuyOrder(StockHandle stock, Timestamp timePlaced, BigDecimal price, int volume) {
		super(stock, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.buyOrderComparitor.compare(this, that);
	}
	
}

