package orderBookReconstructor;

public abstract class Order {
	private String tickerSymbol;
	private int price;
	private int volume;
	private double timestamp; //A floating-point UNIX timestamp of when the order was placed.
	
	public Order(String tickerSymbol, int price, int volume, double timestamp) {
		this.tickerSymbol = tickerSymbol;
		this.price = price;
		this.volume = volume;
		this.timestamp = timestamp;
	}
	
	public String getTickerSymbol() {
		return tickerSymbol;
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
}
