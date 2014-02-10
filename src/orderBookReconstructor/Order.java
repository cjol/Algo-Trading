package orderBookReconstructor;

import testHarness.StockHandle;
import java.sql.Timestamp;

public abstract class Order implements Comparable<Order> {
	private final StockHandle stock;
	private final int price;
	private final int volume;
	private final Timestamp timePlaced;
	private Timestamp timeExecuted;
	
	
	public Order(StockHandle stock, Timestamp timePlaced, int price, int volume) {
		this.stock = stock;
		this.price = price;
		this.volume = volume;
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
	
	public Timestamp getTimePlaced() {
		// Timestamp's are mutable, so return a copy
		return (Timestamp)timePlaced.clone();
	}
	
	protected Timestamp getTimeExecuted() {
		// Timestamp's are mutable, so return a copy
		return (Timestamp)timeExecuted.clone();
	}

	protected void setTimeExecuted(Timestamp timeExecuted) {
		// Timestamp's are mutable, so store a copy
		this.timeExecuted = (Timestamp)timeExecuted.clone();
	}

	@Override
	public int compareTo(Order that) {
		//Compares two orders on timestamps: comparison by price is handled
		//by the PriceLevel object.
		return timePlaced.compareTo(that.timePlaced);
	}
}