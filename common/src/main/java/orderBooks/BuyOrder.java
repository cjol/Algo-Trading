package orderBooks;

import database.StockHandle;

public class BuyOrder extends Order {
	public BuyOrder(StockHandle stock, int price, int volume) {
		super(stock, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.buyOrderComparitor.compare(this, that);
	}
}

