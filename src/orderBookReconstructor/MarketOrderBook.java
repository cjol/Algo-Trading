package orderBookReconstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import Iterators.ProtectedIterator;

import database.StockHandle;
import database.TestDataHandler;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
	public BuyOrder buy(int volume, BigDecimal price, Timestamp time) {
		throw new NotImplementedException();
	}

	@Override
	public SellOrder sell(int volume, BigDecimal price, Timestamp time) {
		throw new NotImplementedException();
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
		bids =
		offers = 
		
		Iterator<Match> matches = 
		this.currentTime = softTime;
		return matches;
	}

	@Override
	public Iterator<SellOrder> getMyOffers() {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<BuyOrder> getMyBids() {
		throw new NotImplementedException();
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
		//FIXME wtf?
		return new HighestBid(null);
	}

	@Override
	public LowestOffer getLowestOffer() {
		updateTime();
		//FIXME
		return new LowestOffer(null);
	}

}
