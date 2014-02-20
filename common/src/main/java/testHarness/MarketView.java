package testHarness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import Iterators.ProtectedIterator;

import orderBooks.Match;
import orderBooks.Order;
import orderBooks.OrderBook;
import orderBooks.OrderBookReconstructor;
import orderBooks.UserOrderBook;
import testHarness.output.Output;
import database.DatasetHandle;
import database.StockHandle;
import database.TestDataHandler;

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
	// TICK_SIZE is in milliseconds
	private final int TICK_SIZE = 500;

	private ITradingAlgorithm algo;
	private Map<StockHandle, Integer> portfolio;
	private Map<StockHandle, Integer> reservedPortfolio;
	private Map<StockHandle,OrderBook> openedBooks;
	private HashSet<UserOrderBook> booksWithPosition;
	private Timestamp currentTime;
	private Timestamp endTime;
	private int numTicks;
	
	private BigDecimal availableFunds;
	private BigDecimal reservedFunds;
	
	private List<Output> outputs; 
	private final TestDataHandler dataHandler;
	private final DatasetHandle dataset;
	private boolean threadShouldBeAborting;
	
	/**
	 * Creates a new MarketView instance for a given algorithm to use
	 * @param algo			The user's trading algorithm
	 * @param outputs		The types of output which this MarketView will log
	 * @param dataHandler	The source of data which this MarketView will use
	 */
	public MarketView(ITradingAlgorithm algo, List<Output> outputs, TestDataHandler dataHandler, DatasetHandle dataset) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
		this.dataset = dataset;
	}
	
	/**
	 * Initialise the MarketView with defaults (will eventually be parameterised). Must be called before any other methods.
	 */
	public void startSimulation() {
		// TODO STARTING_FUNDS and *_TIME should be simulation parameters
		availableFunds = STARTING_FUNDS;
		reservedFunds = new BigDecimal(0);
		
		currentTime = dataset.getStartTime();
		endTime = dataset.getEndTime();
		
		numTicks = 0;
		portfolio = new HashMap<StockHandle, Integer>();
		reservedPortfolio = new HashMap<StockHandle, Integer>();
		booksWithPosition = new HashSet<>();
		openedBooks = new HashMap<>();
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
		//FIXME another exception
		if (!currentTime.before(endTime)) throw new RuntimeException("Simulation over");
		numTicks++;

		Timestamp newTime = new Timestamp(currentTime.getTime() + TICK_SIZE);
		// probably not needed since nanos are never updated
		//newTime.setNanos(currentTime.getNanos()); 
		currentTime = newTime;
		
		for (OrderBook orderBook : openedBooks.values()) {
			orderBook.softSetTime(currentTime);
		}
		
		Iterator<UserOrderBook> bookIter = booksWithPosition.iterator();
		List<Match> matches = new LinkedList<>();
		while(bookIter.hasNext()) {
			UserOrderBook book = bookIter.next();
			
			Iterator<Match> matcheIter = book.updateTime();
			//TODO commision
			while(matcheIter.hasNext()) {
				Match m = matcheIter.next();
				matches.add(m);
				
				if(m.isUserBid) {
					addStockToPortfolio(m.stockHandle, m.quantity);
					reservedFunds.subtract(new BigDecimal(m.price * m.quantity));
				} else if(m.isUserOffer) {
					removeReserveStock(m.stockHandle, m.quantity);
					availableFunds.add(new BigDecimal(m.price * m.quantity));
				}
			}
			
			if(book.isComplete()) bookIter.remove();
		}
		
		//FIXME 
		// update Outputs
		TickData tickdata = new TickData(currentTime, portfolio, null, availableFunds);
		for (Output output : outputs) {
			output.evaluateData(tickdata);
		}
		
		return matches.iterator();
	}

	/**
	 * Called by the user to view the OrderBook for a given stock, in order to determine the current BBO for that stock. 
	 * @param stock A StockHandle for the stock the user wishes to query.
	 * @return An OrderBook representing the market data for the given stock, at the current simulation time.
	 */
	public OrderBook getOrderBook(StockHandle stock) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		if (!openedBooks.containsKey(stock)) {
			OrderBook market = new OrderBookReconstructor(currentTime, stock, dataHandler);
			OrderBook user = new UserOrderBook(stock, market);
			openedBooks.put(stock, user); 	
		}
		return openedBooks.get(stock);
		
	}
	
	/**
	 * Called by the user to determine if the simulation is over.
	 * @return A boolean indicating if the simulation has finished.
	 */
	public boolean isFinished() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return (!currentTime.before(endTime));
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
	public boolean buy(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		
		int totalPrice = price * volume;
		BigDecimal totalBig = (new BigDecimal(totalPrice));
		if (getAvailableFunds().compareTo(totalBig) < 0)
			return false; // we don't have enough funds
		
		reserveFunds(totalBig);
		
		getOrderBook(stock).buy(volume, price, currentTime);
		return true;
	}

	/**
	 * Called by the user to place a sell offer to the market.
	 * @param stock		The stock which the user wants to sell
	 * @param price		The price the user is offering to sell at
	 * @param volume	The amount of stock the user wants to sell
	 * @return Whether the offer was successfully posted (may return false if we have insufficient stock)
	 */
	public boolean sell(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();

		if(!reserveStocks(stock, volume)) return false;
		
		getOrderBook(stock).sell(volume, price, currentTime);
		return true;
	}
	
	private void reserveFunds(BigDecimal amount) {
		availableFunds = availableFunds.subtract(amount);
		reservedFunds = reservedFunds.add(amount);
	}
	
	private boolean reserveStocks(StockHandle stock, int volume) {
		if (!portfolio.containsKey(stock))
			return false; // we don't own any of this stock (for now that means no trade)
		
		int amtOwned = portfolio.get(stock);
		if (amtOwned < volume)
			return false; // we don't own enough for this sale

		// subtract sold stock now - will be returned if we cancel?
		portfolio.put(stock, amtOwned - volume);
		
		int alreadyReserved = reservedPortfolio.containsKey(stock) ? reservedPortfolio.get(stock) : 0;
		reservedPortfolio.put(stock, alreadyReserved + volume);
		
		return true;
	}
	
	private void removeReserveStock(StockHandle stock, int volume) {
		int alreadyReserved = reservedPortfolio.containsKey(stock) ? reservedPortfolio.get(stock) : 0;
		alreadyReserved -= volume;
		assert(alreadyReserved >= 0);
		reservedPortfolio.put(stock, alreadyReserved);
	}
	
	private void addStockToPortfolio(StockHandle stock, int volume) {
		int hasAlready = portfolio.containsKey(stock) ? portfolio.get(stock) : 0;
		hasAlready += volume;
		portfolio.put(stock, hasAlready);
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
		Iterator<StockHandle> res = null;
		try {
			res = dataHandler.getAllStocks(dataset);	
		} catch (SQLException e) {
			// TODO: Handle exceptions in consistent manner, + more intelligently //FIXME
			e.printStackTrace();
			System.exit(-1);
		}
		return res;
	}
	
	/**
	 * Called by the user to get the Orders which have not yet been filled.
	 * @return A new iterator over orders which are still outstanding
	 */
	public Iterator<Order> getOutstandingOrders() {
		//FIXME
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return null;
	}

	/**
	 * Called by the user to get the user's current portfolio (stocks and amounts)
	 * @return A new iterator over Map entries between StockHandle and amounts of stock.
	 */
	public Iterator<Entry<StockHandle, Integer>> getPortfolio() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return new ProtectedIterator<>(portfolio.entrySet().iterator());
	}

	/**
	 * Called by the user to get the stocks for which the user has outstanding orders.
	 * @return A new iterator over stocks which have outstanding orders
	 */
	public Iterator<StockHandle> getStocksWithOutstanding() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		List<StockHandle> out = new LinkedList<StockHandle>();
		for (UserOrderBook o : booksWithPosition) {
			out.add(o.handle);
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
		List<StockHandle> out = new LinkedList<StockHandle>();
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
