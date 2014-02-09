package testHarness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import testHarness.output.Output;

public class MarketView {
	private final int STARTING_FUNDS = 10000;
	// TODO: What is a useful starting time?
	private final Date STARTING_TIME = new Date();
	// TICK_SIZE is in milliseconds
	private final int TICK_SIZE = 10;
	// TODO: What is a useful ending time?
	private final Date ENDING_TIME = new Date();

	private ITradingAlgorithm algo;
	private Map<StockHandle, Integer> portfolio;
	private List<Trade> outstandingTrades;
	private List<Trade> allTrades;
	private Calendar currentTime;
	private int numTicks;
	// NOTE: availableFunds is an int because I heard once that floating point types shouldn't be 
	// used for money. But what is the unit here?
	private int availableFunds;
	private List<Output> outputs; 
	private TestDataHandler dataHandler;
	
	public MarketView(ITradingAlgorithm algo, List<Output> outputs, TestDataHandler dataHandler) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
	}
	
	public void startSimulation() {
		// TODO STARTING_FUNDS and _TIME should be simulation parameters
		availableFunds = STARTING_FUNDS;
		currentTime = Calendar.getInstance();
		currentTime.setTime(STARTING_TIME);
		numTicks = 0;
		allTrades = new ArrayList<Trade>();
		outstandingTrades = new ArrayList<Trade>();
		portfolio = new HashMap<StockHandle, Integer>();
		
		algo.run(this);
	}
	
	public Iterator<Trade> tick() {
		currentTime.add(Calendar.MILLISECOND, TICK_SIZE);
		
		// updateTime on order books
		
//		for (Trade t : outstandingTrades) {
//			OrderBook ob = getOrderBook(t.getStockHandle());
//			ob.updateTime(currentTime.)
//		}
		return null;
		//TODO calls OrderBook.updateTime on OrderBooks for all outstanding trades and returns a list of trades which are still outstanding at this time.

	}
	
	public OrderBook getOrderBook(StockHandle s) {
		return s.getOrderBookAtTime(currentTime.getTime());
	}
	
	public boolean isFinished() {
		return (!currentTime.getTime().before(ENDING_TIME));
	}
	
	public int getRemainingFunds() {
		return availableFunds;
	}
	
	public boolean buy(StockHandle stock, int price, int volume) {
		if (getRemainingFunds() < price * volume)
			return false; // we don't have enough funds
		
		Trade t = getOrderBook(stock).buy(volume, price);
		allTrades.add(t);
		outstandingTrades.add(t);
		return true;
	}
	
	public boolean sell(StockHandle stock, int price, int volume) {
		if (!portfolio.containsKey(stock))
			return false; // we don't own any of this stock (for now that means no trade (TODO?))
		
		int amtOwned = portfolio.get(stock);
		if (amtOwned < volume)
			return false; // we don't own enough for this sale

		Trade t = getOrderBook(stock).sell(volume, price);
		allTrades.add(t);
		outstandingTrades.add(t);
		return true;
	}
	
	public Iterator<StockHandle> getAllStocks() {
		// TODO: Does this need cloning HERE before being handed to the user?
		// probably should determine a policy for where such clones are made so we don't make them a million times
		return dataHandler.getStockHandles();
	}

	public Iterator<Trade> getOutstandingTrades() {
		List<Trade> cloned = new ArrayList<Trade>();
		for (Trade t : outstandingTrades) {
			cloned.add(t);
		}
		return cloned.iterator();
	}
	public Iterator<Entry<StockHandle, Integer>> getPortfolio() {
		Set<Entry<StockHandle, Integer>> cloned = new HashSet<Entry<StockHandle, Integer>>();
		for (Entry<StockHandle, Integer> e : portfolio.entrySet()) {
			cloned.add(e);
		}
		return cloned.iterator();
	}
	
	public Iterator<StockHandle> getStocksWithOutstanding() {
		Set<StockHandle> out = new HashSet<StockHandle>();
		for (Trade t : outstandingTrades) {
			out.add(t.getStockHandle());
		}
		return out.iterator();
	}
	
	public Iterator<StockHandle> getOwnedStocks() {
		Set<StockHandle> out = new HashSet<StockHandle>();
		for (Entry<StockHandle, Integer> e : portfolio.entrySet()) {
			out.add(e.getKey());
		}
		return out.iterator();
	}
	
	
	public void tryCleanAbort(Thread runningThread) {
		//TODO
		//This method should try and cleanly release any locks held by the marketView, and ensure it does not try to take
		//out any more.
	}
}
