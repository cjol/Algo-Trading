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
	
	
	public MarketOrderBook(Timestamp startTime, StockHandle handle, TestDataHandler dataHandler) {
		super(handle);
		this.currentTime = new Timestamp(Long.MIN_VALUE);
		this.softTime = startTime;
		this.dataHandler = dataHandler;
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

}
