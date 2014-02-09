package orderBookReconstructor;

import testHarness.StockHandle;

public abstract class Order implements Comparable<Order> {
	private StockHandle stock;
	private int price;
	private int volume;
	private double timestamp; //A floating-point UNIX timestamp of when the order was placed.
	
	public Order(StockHandle stock, int price, int volume, double timestamp) {
		this.stock = stock;
		this.price = price;
		this.volume = volume;
		this.timestamp = timestamp;
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

	public double getTimestamp() {
		return timestamp;
	}
	
	@Override
	public int compareTo(Order that) {
		//Compares two orders on timestamps: comparison by price is handled
		//by the PriceLevel object.
		if (this.timestamp < that.timestamp) return -1; else return 1;
	}
}
