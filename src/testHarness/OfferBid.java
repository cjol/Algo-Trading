package testHarness;

public class OfferBid{
	private StockHandle stockHandle;
	private int volume;
	private int price;
	//TODO probably needs an owner ID as well

	public void cancel() {
		//TODO Note, cannot cancel other peoples bids.
		//Maybe the MarketView should have a method cancel(order) since it
		//keeps track of player's orders, whereas for the orders from the 
		//actual market feed-derived order book we remove the cancel method alltogether.
	}
	
	public int getVolume() {
		return volume;
	}
	
	public int getPrice() {
		return price;
	}
	
	public StockHandle getStockHandle() {
		return stockHandle;
	}
	
	public OfferBid (StockHandle stockHandle, int volume, int price) {
		this.stockHandle = stockHandle;
		this.volume = volume;
		this.price = price;
	}
}
