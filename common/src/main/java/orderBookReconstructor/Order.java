package orderBookReconstructor;

import java.util.Comparator;

import database.StockHandle;

public abstract class Order implements Comparable<Order>, Cloneable {
	private final StockHandle stock;
	private final int price;
	private int volume;
	
	public Order(StockHandle stock, int price, int volume) {
		this.stock = stock;
		this.price = price;
		this.volume = volume;
		if (price < 0) {
			throw new AssertionError("Invalid price");
		}
		if (volume < 0) {
			throw new AssertionError("Invalid volume");
		}
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
	
	protected void incrementVolume(int amount) {
		this.volume += amount;
	}
	
	//No ordering makes sense here, much better to define the ordering lower down
	@Override
	public abstract int compareTo(Order that);
	
	public Order clone() throws CloneNotSupportedException {
		//Cloning the timestamp not required here: getTimestamp() returns a clone anyway
		return (Order) super.clone();
	}
	
	static class BuyOrderComparator<T extends Order> implements Comparator<T> {

		@Override
		public int compare(Order o1, Order that) {
			return Integer.compare(o1.getPrice(),that.getPrice());
		}
	}
	
	static class SellOrderComparator<T extends Order> implements Comparator<T> {

		@Override
		public int compare(Order o1, Order o2) {
			return Integer.compare(o2.getPrice(),o1.getPrice());
		}
	}
	
	public static final Comparator<Order> buyOrderComparitor = new BuyOrderComparator<Order>();
	public static final Comparator<Order> sellOrderComparitor = new SellOrderComparator<Order>();
	
	public static final Comparator<BuyOrder> buyOrderOnlyComparator = new BuyOrderComparator<BuyOrder>();
	public static final Comparator<SellOrder> sellOrderOnlyComparator = new SellOrderComparator<SellOrder>();
}