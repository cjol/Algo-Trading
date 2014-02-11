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
		this.volume -= match;
	}

	public Timestamp getTimePlaced() {
		// Timestamp's are mutable, so return a copy
		return (Timestamp)timePlaced.clone();
	}
	
	//No ordering makes sense here, much better to define the ordering lower down
	@Override
	public abstract int compareTo(Order that);
	
	public Order clone() throws CloneNotSupportedException {
		//Cloning the timestamp not required here: getTimestamp() returns a clone anyway
		return (Order) super.clone();
	}
	
	static class BuyOrderComparitor<T extends Order> implements Comparator<T> {

		@Override
		public int compare(Order o1, Order that) {
			int com = Integer.compare(o1.getPrice(), that.getPrice());
			return (com == 0) ? that.getTimePlaced().compareTo(o1.getTimePlaced()) : com;
		}
	}
	
	static class SellOrderComparitor<T extends Order> implements Comparator<T> {

		@Override
		public int compare(Order o1, Order o2) {
			int com = Integer.compare(o2.getPrice(), o1.getPrice());
			return (com == 0) ? o2.getTimePlaced().compareTo(o1.getTimePlaced()) : com;
		}
	}
	
	public static final Comparator<Order> buyOrderComparitor = new BuyOrderComparitor<Order>();
	public static final Comparator<Order> sellOrderComparitor = new SellOrderComparitor<Order>();
	
	public static final Comparator<BuyOrder> buyOrderOnlyComparitor = new BuyOrderComparitor<BuyOrder>();
	public static final Comparator<SellOrder> sellOrderOnlyComparitor = new SellOrderComparitor<SellOrder>();
}