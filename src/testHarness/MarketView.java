package testHarness;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

import orderBookReconstructor.Order;
import testHarness.output.Output;

public class MarketView {
	private final BigDecimal STARTING_FUNDS = new BigDecimal(10000);
	// TODO: What is a useful starting time?
	private final Timestamp STARTING_TIME = new Timestamp(0);
	// TICK_SIZE is in milliseconds
	private final int TICK_SIZE = 10;
	// TODO: What is a useful ending time?
	private final Date ENDING_TIME = new Timestamp(0);

	private ITradingAlgorithm algo;
	private Map<StockHandle, Integer> portfolio;
	private List<Order> outstandingOrders;
	private Map<StockHandle,OrderBook> openedBooks;
	private List<Order> allOrders;
	private Timestamp currentTime;
	private int numTicks;
	private BigDecimal availableFunds;
	private List<Output> outputs; 
	private TestDataHandler dataHandler;
	
	public MarketView(ITradingAlgorithm algo, List<Output> outputs, TestDataHandler dataHandler) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
	}
	
	public void startSimulation() {
		// TODO STARTING_FUNDS and *_TIME should be simulation parameters
		availableFunds = STARTING_FUNDS;
		currentTime = (Timestamp) STARTING_TIME.clone();
		numTicks = 0;
		allOrders = new ArrayList<Order>();
		outstandingOrders = new ArrayList<Order>();
		portfolio = new HashMap<StockHandle, Integer>();
		
		algo.run(this);
	}
	
	public Iterator<Order> tick() {
		numTicks++;

		Timestamp newTime = new Timestamp(currentTime.getTime() + TICK_SIZE);
		// probably not needed since nanos are never updated
		newTime.setNanos(currentTime.getNanos()); 
		
		//TODO calls OrderBook.updateTime on OrderBooks for all outstanding trades and returns a list of trades which are still outstanding at this time.
		
		return null;


	}
	
	public OrderBook getOrderBook(StockHandle s) {
		if (openedBooks.containsKey(s))
			return openedBooks.get(s);
		return s.getOrderBookAtTime(currentTime);
	}
	
	public boolean isFinished() {
		return (!currentTime.before(ENDING_TIME));
	}
	
	public BigDecimal getRemainingFunds() {
		return availableFunds;
	}
	
	public boolean buy(StockHandle stock, int price, int volume) {
		if (getRemainingFunds().compareTo(new BigDecimal(price * volume)) < 0)
			return false; // we don't have enough funds
		
		Order o = getOrderBook(stock).buy(volume, price);
		allOrders.add(o);
		outstandingOrders.add(o);
		return true;
	}
	
	public boolean sell(StockHandle stock, int price, int volume) {
		if (!portfolio.containsKey(stock))
			return false; // we don't own any of this stock (for now that means no trade (TODO?))
		
		int amtOwned = portfolio.get(stock);
		if (amtOwned < volume)
			return false; // we don't own enough for this sale

		Order o = getOrderBook(stock).sell(volume, price);
		allOrders.add(o);
		outstandingOrders.add(o);
		return true;
	}
	
	public Iterator<StockHandle> getAllStocks() {
		// TODO: Does this need cloning HERE before being handed to the user?
		// probably should determine a policy for where such clones are made so we don't make them a million times
		return dataHandler.getStockHandles();
	}

	public Iterator<Order> getOutstandingOrders() {
		List<Order> cloned = new ArrayList<Order>();
		for (Order o : outstandingOrders) {
			cloned.add(o);
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
		for (Order o : outstandingOrders) {
			out.add(o.getStockHandle());
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
