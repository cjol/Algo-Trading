package testHarness;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.PriorityQueue;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

public class UserOrderBook extends OrderBook {

	OrderBook parent;
	
	PriorityQueue<BuyOrder> OutstandingBids;
	PriorityQueue<SellOrder> OutstandingOffers;
	
	public UserOrderBook(StockHandle handle, OrderBook parent) {
		super(handle);
		this.parent = parent;
		
		OutstandingBids = new PriorityQueue<>(0,new Order.BuyOrderComparitor());
		OutstandingOffers = new PriorityQueue<>(0,new Order.SellOrderComparitor());
		
	}

	@Override
	public BuyOrder buy(int volume, int price) {
		BuyOrder bo = new BuyOrder(handle, , price, volume)
		return null;
	}

	@Override
	public SellOrder sell(int volume, int price) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<BuyOrder> getAllBids() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Match> updateTime(Timestamp t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HighestBid getHighestBid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LowestOffer getLowestOffer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
