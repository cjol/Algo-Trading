package orderBookReconstructor;

import testHarness.StockHandle;

import java.sql.Timestamp;
import java.util.Comparator;

public abstract class Order implements Comparable<Order>, Cloneable {
	private final StockHandle stock;
	private final int price;
	private int volume;
	private final Timestamp timePlaced;
	
	public Order(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		this.stock = stock;
		this.price = price;
		this.volume = volume;
		if (this.price <= 0) {
			throw new AssertionError("Invalid price");
		}
		if (this.volume <= 0) {
			throw new AssertionError("Invalid volume");
		}
		this.timePlaced = timePlaced;
	}
	
	public StockHandle getStockHandle() {
		return stock;
	}

	public int getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}
	
	protected void decrementVolume(int match) {
		if (match > this.volume) {
			throw new AssertionError("match greater than volume");
		}
		this.volume -= volume;
	}

	public Timestamp getTimePlaced() {
		// Timestamp's are mutable, so return a copy
		return (Timestamp)timePlaced.clone();
	}
	
	@Override
	public int compareTo(Order that) {
		//Compares two orders on timestamps: comparison by price is handled
		//by the PriceLevel object.
		return timePlaced.compareTo(that.timePlaced);
	}
	
	public Order clone() throws CloneNotSupportedException {
		//Cloning the timestamp not required here: getTimestamp() returns a clone anyway
		return (Order) super.clone();
	}
	
	public static class BuyOrderComparitor implements Comparator<BuyOrder> {

		@Override
		public int compare(BuyOrder o1, BuyOrder o2) {
			int com = Integer.compare(o1.getPrice(), o2.getPrice());
			return (com == 0) ? o2.getTimePlaced().compareTo(o1.getTimePlaced()) : com;
		}
	}
	
	public static class SellOrderComparitor implements Comparator<BuyOrder> {

		@Override
		public int compare(BuyOrder o1, BuyOrder o2) {
			int com = Integer.compare(o2.getPrice(), o1.getPrice());
			return (com == 0) ? o2.getTimePlaced().compareTo(o1.getTimePlaced()) : com;
		}
	}
}