package testHarness;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import orderBookReconstructor.Match;
import orderBookReconstructor.Order;
import testHarness.output.Output;

/**
 * Thrown at the user when their algorithm tries to access any function after the thread has been told to abort.
 * @author Christopher Little
 */
class SimulationAbortedException extends RuntimeException{}
/**
 * Allows the user's algorithm to interact with historical market data, both for reading and posting orders.
 * Also logs the user's actions for calculating how well the algorithm performs.
 * @author Christopher Little
 */
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
	private boolean threadShouldBeAborting;
	
	/**
	 * Creates a new MarketView instance for a given algorithm to use
	 * @param algo			The user's trading algorithm
	 * @param outputs		The types of output which this MarketView will log
	 * @param dataHandler	The source of data which this MarketView will use
	 */
	public MarketView(ITradingAlgorithm algo, List<Output> outputs, TestDataHandler dataHandler) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
	}
	
	/**
	 * Initialise the MarketView with defaults (will eventually be parameterised). Must be called before any other methods.
	 */
	public void startSimulation() {
		// TODO STARTING_FUNDS and *_TIME should be simulation parameters
		availableFunds = STARTING_FUNDS;
		currentTime = (Timestamp) STARTING_TIME.clone();
		numTicks = 0;
		allOrders = new ArrayList<Order>();
		outstandingOrders = new ArrayList<Order>();
		portfolio = new HashMap<StockHandle, Integer>();
		threadShouldBeAborting = false;
		
		algo.run(this);
	}
	
	/**
	 * Called by the user's algorithm at every iteration. The MarketView then updates its representation of time, 
	 * and increments the simulation as necessary (updating the user's portfolio and funds).
	 * @return An iterator over Matches which were made since the user last called tick().
	 */
	public Iterator<Match> tick() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		numTicks++;

		Timestamp newTime = new Timestamp(currentTime.getTime() + TICK_SIZE);
		// probably not needed since nanos are never updated
		newTime.setNanos(currentTime.getNanos()); 
		currentTime = newTime;
		List<Match> allMatches = new ArrayList<Match>();
		
		// Update simulation state, based on the updated results of every outstanding order
		for (Order order : outstandingOrders) {
			// TODO: Would be tidier if there was a straight link from Order to OrderBook!
			StockHandle stock = order.getStockHandle();
			OrderBook orderbook = getOrderBook(stock);
			Iterator<Match> matches = orderbook.updateTime(currentTime);
			while(matches.hasNext()) {
				Match match = matches.next();
				
				// FIXME Only if Orders are uniquely represented then I can compare references..
				if (order == match.buyOrder) {
					// I made the buy order - give me my stock!
					int amtOwned = match.quantity;
					if (portfolio.containsKey(stock))
						amtOwned += portfolio.get(stock);
					portfolio.put(stock, amtOwned);

					// TODO: Take commission?
					
				} else if (order == match.sellOrder) {
					// I made the sell order - give me monies!
					availableFunds.add( match.price.multiply(new BigDecimal(match.quantity)) );

					// TODO: Take commission?
					
				} else {
					// This order didn't concern me, so I can just ignore it
				}
				
				// FIXME: Again, reliant on Orders being uniquely represented
				if (order.getVolume() < 1)
					outstandingOrders.remove(order);
				allMatches.add(match);
			}	
		}
		
		// update Outputs
		TickData tickdata = new TickData(currentTime, portfolio, outstandingOrders, availableFunds);
		for (Output output : outputs) {
			output.evaluateData(tickdata);
		}
		
		return allMatches.iterator();
	}

	/**
	 * Called by the user to view the OrderBook for a given stock, in order to determine the current BBO for that stock. 
	 * @param stock A StockHandle for the stock the user wishes to query.
	 * @return An OrderBook representing the market data for the given stock, at the current simulation time.
	 */
	public OrderBook getOrderBook(StockHandle stock) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		if (openedBooks.containsKey(stock))
			return openedBooks.get(stock);
		return stock.getOrderBookAtTime(currentTime);
	}
	
	/**
	 * Called by the user to determine if the simulation is over.
	 * @return A boolean indicating if the simulation has finished.
	 */
	public boolean isFinished() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return (!currentTime.before(ENDING_TIME));
	}
	
	/**
	 * Gets the user's remaining funds
	 * @return the user's current available funds.
	 */
	public BigDecimal getAvailableFunds() {
		if (threadShouldBeAborting)
			return null;
		return availableFunds;
	}
		
	// TODO: The user needs to be able to cancel an order!
	
	/**
	 * Called by the user to place a buy offer to the market.
	 * @param stock		The stock which the user wants to buy
	 * @param price		The price the user is offering to buy at
	 * @param volume	The amount of stock the user wants to buy
	 * @return Whether the offer was successfully posted (may return false if we have insufficient funds)
	 */
	public boolean buy(StockHandle stock, BigDecimal price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		
		if (getAvailableFunds().compareTo(price.multiply(new BigDecimal(volume))) < 0)
			return false; // we don't have enough funds
		
		// subtract available funds now - will be returned if we cancel?
		availableFunds.add( price.negate().multiply(new BigDecimal(volume)) );
		
		Order o = getOrderBook(stock).buy(volume, price, currentTime);
		allOrders.add(o);
		outstandingOrders.add(o);
		return true;
	}

	/**
	 * Called by the user to place a sell offer to the market.
	 * @param stock		The stock which the user wants to sell
	 * @param price		The price the user is offering to sell at
	 * @param volume	The amount of stock the user wants to sell
	 * @return Whether the offer was successfully posted (may return false if we have insufficient stock)
	 */
	public boolean sell(StockHandle stock, BigDecimal price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		if (!portfolio.containsKey(stock))
			return false; // we don't own any of this stock (for now that means no trade)

		int amtOwned = portfolio.get(stock);
		if (amtOwned - volume < 0)
			return false; // we don't own enough for this sale

		// subtract sold stock now - will be returned if we cancel?
		portfolio.put(stock, amtOwned - volume);
		
		Order o = getOrderBook(stock).sell(volume, price, currentTime);
		allOrders.add(o);
		outstandingOrders.add(o);
		return true;
	}
	
	/**
	 * Called by the user to determine which stocks are available to trade.
	 * @return An iterator over StockHandles for all stocks available in this market 
	 */
	public Iterator<StockHandle> getAllStocks() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		// TODO: Does this need cloning HERE before being handed to the user?
		// probably should determine a policy for where such clones are made so we don't make them a million times
		return dataHandler.getStockHandles();
	}
	
	/**
	 * Called by the user to get the Orders which have not yet been filled.
	 * @return A new iterator over orders which are still outstanding
	 */
	public Iterator<Order> getOutstandingOrders() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		List<Order> cloned = new ArrayList<Order>();
		for (Order o : outstandingOrders) {
			cloned.add(o);
		}
		return cloned.iterator();
	}

	/**
	 * Called by the user to get the user's current portfolio (stocks and amounts)
	 * @return A new iterator over Map entries between StockHandle and amounts of stock.
	 */
	public Iterator<Entry<StockHandle, Integer>> getPortfolio() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		Set<Entry<StockHandle, Integer>> cloned = new HashSet<Entry<StockHandle, Integer>>();
		for (Entry<StockHandle, Integer> e : portfolio.entrySet()) {
			cloned.add(e);
		}
		return cloned.iterator();
	}

	/**
	 * Called by the user to get the stocks for which the user has outstanding orders.
	 * @return A new iterator over stocks which have outstanding orders
	 */
	public Iterator<StockHandle> getStocksWithOutstanding() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		Set<StockHandle> out = new HashSet<StockHandle>();
		for (Order o : outstandingOrders) {
			out.add(o.getStockHandle());
		}
		return out.iterator();
	}
	
	/**
	 * Get a simple iterator over all StockHandles the user owns any of.
	 * @return A new iterator over stocks which the user has some of.
	 */
	public Iterator<StockHandle> getOwnedStocks() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		Set<StockHandle> out = new HashSet<StockHandle>();
		for (Entry<StockHandle, Integer> e : portfolio.entrySet()) {
			out.add(e.getKey());
		}
		return out.iterator();
	}
	
	/**
	 * Called when the user's algorithm should terminate, to try to encourage him to.
	 * @param runningThread 
	 */
	public void tryCleanAbort(Thread runningThread) {
		threadShouldBeAborting = true;
		// TODO: Anything else?
	}
}
