package testHarness;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Match;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

public class UserOrderBook extends OrderBook {

	OrderBook parent;
	
	TreeSet<BuyOrder> outstandingBids;
	TreeSet<SellOrder> outstandingOffers;
	
	TreeSet<BuyOrder> ghostBids;
	TreeSet<SellOrder> ghostOffers;
	
	public UserOrderBook(StockHandle handle, OrderBook parent) {
		super(handle);
		this.parent = parent;
		
		outstandingBids = new TreeSet<BuyOrder>(new Order.BuyOrderComparitor());
		outstandingOffers = new TreeSet<SellOrder>(new Order.SellOrderComparitor());
	}

	@Override
	public BuyOrder buy(int volume, int price, Timestamp time) {
		BuyOrder bo = new BuyOrder(handle,time, price, volume);
		outstandingBids.add(bo);
		return bo;
	}

	@Override
	public SellOrder sell(int volume, int price, Timestamp time) {
		SellOrder so = new SellOrder(handle,time, price, volume);
		OutstandingOffers.add(so);
		return so;
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
		//TODO 
		Iterator<Match> matches = parent.updateTime(t);
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
