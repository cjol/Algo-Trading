package orderBookReconstructor;

import java.util.Map;

/*
 * Represents an order book snapshot at a certain time.
 * For the purposes of testing so far; normal methods don't have
 * definitions that are larger than the implementation.
 */
public class OrderBookReconstructorResult {
	private Map<Integer, PriceLevel<BuyOrder>> stockBids;
	private Map<Integer, PriceLevel<SellOrder>> stockOffers;
	
	public OrderBookReconstructorResult(Map<Integer, PriceLevel<BuyOrder>> stockBids,
			Map<Integer, PriceLevel<SellOrder>> stockOffers) {
		this.stockBids = stockBids;
		this.stockOffers = stockOffers;
	}
	
	public Map<Integer, PriceLevel<BuyOrder>> getBidsMap() {return stockBids;}
	public Map<Integer, PriceLevel<SellOrder>> getOffersMap() {return stockOffers;}
	
}
