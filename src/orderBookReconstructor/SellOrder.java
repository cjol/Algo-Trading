package orderBookReconstructor;

public class SellOrder extends Order {
	public SellOrder(String tickerSymbol, int price, int volume, double timestamp) {
		super(tickerSymbol, price, volume, timestamp);
	}
}
