package orderBookReconstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;

import database.StockHandle;

public abstract class Order implements Comparable<Order>, Cloneable {
	private final StockHandle stock;
	private final BigDecimal price;
	private int volume;
	
	public Order(StockHandle stock, BigDecimal price, int volume) {
		this.stock = stock;
		this.price = price;
		this.volume = volume;
		if (this.price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new AssertionError("Invalid price");
		}
		if (this.volume <= 0) {
			throw new AssertionError("Invalid volume");
		}
	}
	
	public StockHandle getStockHandle() {
		return stock;
	}

	public BigDecimal getPrice() {
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
			return o1.getPrice().compareTo(that.getPrice());
		}
	}
	
	static class SellOrderComparator<T extends Order> implements Comparator<T> {

		@Override
		public int compare(Order o1, Order o2) {
			return o2.getPrice().compareTo(o1.getPrice());
		}
	}
	
	public static final Comparator<Order> buyOrderComparitor = new BuyOrderComparator<Order>();
	public static final Comparator<Order> sellOrderComparitor = new SellOrderComparator<Order>();
	
	public static final Comparator<BuyOrder> buyOrderOnlyComparator = new BuyOrderComparator<BuyOrder>();
	public static final Comparator<SellOrder> sellOrderOnlyComparator = new SellOrderComparator<SellOrder>();
}