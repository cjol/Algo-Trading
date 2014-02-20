package orderBooks;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + price;
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		result = prime * result + volume;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return price == other.price && volume == other.volume;
	}

	@Override
	public String toString() {
		return String.format("Order for %s of %d at %d", stock.toString(),
							volume, price);
	}
}