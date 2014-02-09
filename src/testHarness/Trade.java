package testHarness;

import java.sql.Timestamp;

public class Trade {
	public enum Type { BID, ASK };
	
	private final StockHandle stock;
	private final Timestamp timestamp;
	private final Type type;
	private final int price, volume;
	
	public Trade(StockHandle stock, Timestamp timestamp, 
				 Type type, int price, int volume) {
		this.stock = stock;
		this.timestamp = timestamp;
		this.type = type;
		this.price = price;
		this.volume = volume;
	}

	public StockHandle getStock() {
		return stock;
	}

	public Type getType() {
		return type;
	}

	public int getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}
	
}
