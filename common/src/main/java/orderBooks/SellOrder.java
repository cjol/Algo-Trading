package orderBooks;

import database.StockHandle;

public class SellOrder extends Order {
	public SellOrder(StockHandle stock, int price, int volume) {
		super(stock, price, volume);
	}

	@Override
	public int compareTo(Order that) {
		return Order.sellOrderComparitor.compare(this, that);
	}
	
	@Override
	public String toString() {
		return "Sell" + super.toString();
	}
}
