package orderBookReconstructor;

public class BuyOrder extends Order {
	public BuyOrder(String tickerSymbol, int price, int volume, double timestamp) {
		super(tickerSymbol, price, volume, timestamp);
	}
}
