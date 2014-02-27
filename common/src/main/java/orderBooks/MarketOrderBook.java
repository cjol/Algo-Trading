package orderBooks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import Iterators.ProtectedIterator;
import database.Pair;
import database.StockHandle;
import database.TestDataHandler;
import testHarness.SimulationAbortedException;
import valueObjects.HighestBid;
import valueObjects.LowestOffer;

public class MarketOrderBook extends OrderBook {
	 
	private final TestDataHandler dataHandler;
	
	private List<BuyOrder> bids;
	private List<SellOrder> offers;
	
	private int tickSize;
	
	//Invariant: customBid holds the list of bids at lastCustomBidOffer
	//			 customOffer holds the list of offers at lastCustomBidOffer
	private Timestamp lastCustomBidOffer;
	private List<BuyOrder> customBids;
	private List<SellOrder> customOffers;
	
	public MarketOrderBook(Timestamp startTime, StockHandle handle, TestDataHandler dataHandler, int tickSize) {
		super(handle);
		this.currentTime = new Timestamp(OrderBook.MinTimestamp);
		this.softTime = startTime;
		this.dataHandler = dataHandler;
		this.tickSize = tickSize;
	}

	@Override
	public BuyOrder buy(int volume, int price, Timestamp time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SellOrder sell(int volume, int price, Timestamp time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<BuyOrder> getAllBids() {
		return getOtherBids();
	}

	@Override
	public Iterator<SellOrder> getAllOffers() {
		return getOtherOffers();
	}
	
	//Returns the bids and offers iterator at any time for the purposes of the value objects.
	//Reuses the interface provided by the database.
	//Is probably extremely slow, especially if using things that evaluate
	//value objects very often (like MovingAverage). Tries to save some time by exploiting
	//the fact that the database returns bids and offers, so if we ask for the bids' and offers'
	//iterator several times for the same timestamp, no extra work will be done.
	private void fetchCustomBidOffer(Timestamp ts) {
		Pair<List<BuyOrder>, List<SellOrder>> pair;
		try {
			pair = dataHandler.getLastOrderSnapshot(handle, ts);
		} catch (SQLException e) {
			throw new SimulationAbortedException(e);
		}
		customBids = pair.first;
		customOffers = pair.second;
		lastCustomBidOffer = ts;
	}
	
	public Iterator<BuyOrder> getBidsAtTicksAgo(int ticksAgo) {
		if (ticksAgo == 0) return getAllBids();
		Timestamp ts = new Timestamp(currentTime.getTime() - tickSize * ticksAgo);
		
		if (!ts.equals(lastCustomBidOffer)) {
			fetchCustomBidOffer(ts);
		}
		
		if (customBids == null) return null;
		return new ProtectedIterator<>(customBids.iterator());
	}
	
	public Iterator<SellOrder> getOffersAtTicksAgo(int ticksAgo) {
		if (ticksAgo == 0) return getAllOffers();
		Timestamp ts = new Timestamp(currentTime.getTime() - tickSize * ticksAgo);
		
		if (!ts.equals(lastCustomBidOffer)) {
			fetchCustomBidOffer(ts);
		}
		
		if (customOffers == null) return null;
		return new ProtectedIterator<>(customOffers.iterator());
	}
	
	@Override
	public Iterator<Match> updateTime() {
		if(currentTime.equals(softTime)) return null;
		try
		{
			//get order state
			Pair<List<BuyOrder>, List<SellOrder>> pair = dataHandler.getLastOrderSnapshot(handle, softTime);
			bids = pair.first;
			offers = pair.second;
			
			//return matches
			Iterator<Match> matches = dataHandler.getMatches(handle, currentTime, softTime);
			this.currentTime = softTime;
			return matches;
		} catch (SQLException e) {
			throw new SimulationAbortedException(e);
		}
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<BuyOrder> getOtherBids() {
		updateTime();
		return new ProtectedIterator<>(bids.iterator());
	}

	@Override
	public Iterator<SellOrder> getOtherOffers() {
		updateTime();
		return new ProtectedIterator<>(offers.iterator());
	}

	@Override
	public HighestBid getHighestBid() {
		updateTime();
		return new HighestBid(this);
	}

	@Override
	public LowestOffer getLowestOffer() {
		updateTime();
		return new LowestOffer(this);
	}

	@Override
	public boolean CancelBuyOrder(int volume, int price) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean CancelSellOrder(int volume, int price) {
		throw new UnsupportedOperationException();
	}

}
