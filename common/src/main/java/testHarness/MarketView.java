package testHarness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import orderBooks.BuyOrder;
import orderBooks.MarketOrderBook;
import orderBooks.Match;
import orderBooks.OrderBook;
import orderBooks.SellOrder;
import orderBooks.UserOrderBook;
import testHarness.clientConnection.Options;
import testHarness.output.Output;
import Iterators.MultiIterator;
import Iterators.ProtectedIterator;
import database.DatasetHandle;
import database.StockHandle;
import database.TestDataHandler;

/**
 * Allows the user's algorithm to interact with historical market data, both for reading and posting orders.
 * Also logs the user's actions for calculating how well the algorithm performs.
 * @author Christopher Little
 */
public class MarketView {
	private final BigDecimal STARTING_FUNDS;
	// TICK_SIZE is in milliseconds
	private final int TICK_SIZE;
	private final BigDecimal COMMISSION_RATE;

	private final ITradingAlgorithm algo;
	private final List<Output> outputs; 
	private final TestDataHandler dataHandler;
	private final DatasetHandle dataset;
	private final Options options;
	
	private final Map<StockHandle, Integer> portfolio = new HashMap<StockHandle, Integer>();
	private final Map<StockHandle, Integer> reservedPortfolio = new HashMap<StockHandle, Integer>();
	private final Map<StockHandle,UserOrderBook> openedBooks = new HashMap<>();
	private final HashSet<UserOrderBook> booksWithPosition = new HashSet<>();
	
	private Timestamp currentTime;
	private Timestamp endTime; 
	
	private BigDecimal availableFunds;
	private BigDecimal reservedFunds = new BigDecimal(0);

	private volatile boolean threadShouldBeAborting = false;
	private int remaining_ticks;
	
	/**
	 * Creates a new MarketView instance for a given algorithm to use
	 * 
	 * @param algo
	 *            The user's trading algorithm
	 * @param outputs
	 *            The types of output which this MarketView will log
	 * @param dataHandler
	 *            The source of data which this MarketView will use
	 */
	public MarketView(ITradingAlgorithm algo, List<Output> outputs,
			TestDataHandler dataHandler, DatasetHandle dataset, Options options) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
		this.dataset = dataset;
		this.options = options;
		
		STARTING_FUNDS = new BigDecimal(options.startingFunds);
		TICK_SIZE = options.tickSize;
		BigDecimal commisionRate = new BigDecimal(options.commissionRate);
		COMMISSION_RATE = commisionRate.divide(new BigDecimal(10000));
		remaining_ticks = options.maxTicks;
	}

	/**
	 * Initialise the MarketView with defaults (will eventually be
	 * parameterised). Must be called before any other methods.
	 */
	void startSimulation() {
		availableFunds = STARTING_FUNDS;
		currentTime = dataset.getStartTime();
		endTime = dataset.getEndTime();
		
		algo.run(this, options);
	}

	/**
	 * Called by the user's algorithm at every iteration. The MarketView then
	 * updates its representation of time, and increments the simulation as
	 * necessary (updating the user's portfolio and funds).
	 * 
	 * @return An iterator over Matches which were made since the user last
	 *         called tick().
	 */
	public Iterator<Match> tick() {
		remaining_ticks--;
		
		if (threadShouldBeAborting) {
			throw new SimulationAbortedException();	
		}
		try
		{
			//FIXME another exception
			if (!currentTime.before(endTime)) throw new RuntimeException("Simulation over");
	
			Timestamp newTime = new Timestamp(currentTime.getTime() + TICK_SIZE);
	
			currentTime = newTime;
			
			for (OrderBook orderBook : openedBooks.values()) {
				orderBook.softSetTime(currentTime);
			}
			
			Iterator<UserOrderBook> bookIter = booksWithPosition.iterator();
			List<Match> matches = new LinkedList<>();
			while(bookIter.hasNext()) {
				UserOrderBook book = bookIter.next();
				
				Iterator<Match> matcheIter = book.updateTime();
				while(matcheIter.hasNext()) {
					Match m = matcheIter.next();
					matches.add(m);
					
					BigDecimal amount = new BigDecimal(m.price * m.quantity);
					BigDecimal commission = calculateCommission(amount);
					if(m.isUserBid) {
						addStockToPortfolio(m.stockHandle, m.quantity);
						reservedFunds = reservedFunds.subtract(amount.add(commission));
					} else if(m.isUserOffer) {
						removeReserveStock(m.stockHandle, m.quantity);
						availableFunds = availableFunds.add(amount.subtract(commission));
					}
				}
				
				//All orders are fill-or-kill (hack for the demo to work around
				//the limitations of the dataset).
				Iterator<BuyOrder> bids = book.getMyBids();
				while (bids.hasNext()) {
					BuyOrder bo = bids.next();
					cancelBuy(bo.getStockHandle(), bo.getPrice(), bo.getVolume());
				}
				
				Iterator<SellOrder> offers = book.getMyOffers();
				while (offers.hasNext()) {
					SellOrder so = offers.next();
					cancelSell(so.getStockHandle(), so.getPrice(), so.getVolume());
				}
				
				if(book.isComplete()) bookIter.remove();
			}
			
			// update Outputs
			TickData tickdata = new TickData(this.TICK_SIZE, this.portfolio, this.reservedPortfolio, this.booksWithPosition, this.currentTime, this.availableFunds, this.reservedFunds, matches);
			for (Output output : outputs) {
				output.evaluateData(tickdata);
			}
			
			return matches.iterator();
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}

	/**
	 * Called by the user to view the OrderBook for a given stock, in order to
	 * determine the current BBO for that stock.
	 * 
	 * @param stock
	 *            A StockHandle for the stock the user wishes to query.
	 * @return An OrderBook representing the market data for the given stock, at
	 *         the current simulation time.
	 */
	public UserOrderBook getOrderBook(StockHandle stock) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		if (!openedBooks.containsKey(stock)) {
			OrderBook market = new MarketOrderBook(currentTime, stock, dataHandler, TICK_SIZE);
			UserOrderBook user = new UserOrderBook(stock, market);
			openedBooks.put(stock, user);
		}
		return openedBooks.get(stock);
	}

	/**
	 * Called by the user to determine if the simulation is over.
	 * 
	 * @return A boolean indicating if the simulation has finished.
	 */
	public boolean isFinished() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return (!currentTime.before(endTime) || remaining_ticks <= 0);
	}

	/**
	 * Gets the user's remaining funds
	 * 
	 * @return the user's current available funds.
	 */
	public BigDecimal getAvailableFunds() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return availableFunds;
	}
	
	public BigDecimal calculateCommission(BigDecimal amount) {
		return amount.multiply(COMMISSION_RATE);
	}
	
	public BigDecimal getFundsLessCommission() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return availableFunds.subtract(calculateCommission(availableFunds));
	}

	/**
	 * Called by the user to place a buy offer to the market.
	 * 
	 * @param stock
	 *            The stock which the user wants to buy
	 * @param price
	 *            The price the user is offering to buy at
	 * @param volume
	 *            The amount of stock the user wants to buy
	 * @return Whether the offer was successfully posted (may return false if we
	 *         have insufficient funds)
	 */
	public boolean buy(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		try
		{
			BigDecimal totalPrice = new BigDecimal(price * volume);
			BigDecimal toReserve = totalPrice.add(calculateCommission(totalPrice));
			
			if(!reserveFunds(toReserve)) return false;
			
			UserOrderBook book = (UserOrderBook) getOrderBook(stock);
			book.buy(volume, price, currentTime);
			booksWithPosition.add(book);
			return true;
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}

	/**
	 * Called by the user to place a sell offer to the market.
	 * 
	 * @param stock
	 *            The stock which the user wants to sell
	 * @param price
	 *            The price the user is offering to sell at
	 * @param volume
	 *            The amount of stock the user wants to sell
	 * @return Whether the offer was successfully posted (may return false if we
	 *         have insufficient stock)
	 */
	public boolean sell(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		try
		{
			if(!reserveStocks(stock, volume)) return false;
			
			UserOrderBook book = (UserOrderBook) getOrderBook(stock);
			book.sell(volume, price, currentTime);
			booksWithPosition.add(book);
			return true;
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}
	
	
	public boolean cancelBuy(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		try
		{
			OrderBook ob = getOrderBook(stock);
			if(ob.CancelBuyOrder(volume, price)) {
				BigDecimal totalPrice = new BigDecimal(volume * price); 
				BigDecimal toRefund = totalPrice.add(calculateCommission(totalPrice)); 
				refundFunds(toRefund);
				return true;
			} else return false;
			
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}
	
	public boolean cancelSell(StockHandle stock, int price, int volume) {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		try
		{
			OrderBook ob = getOrderBook(stock);
			if(ob.CancelSellOrder(volume, price)) {
				refundStocks(stock, volume);
				return true;
			} else return false;
			
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}

	private boolean reserveFunds(BigDecimal totalPrice) {
		if (getAvailableFunds().compareTo(totalPrice) < 0)
			return false; // we don't have enough funds
		
		availableFunds = availableFunds.subtract(totalPrice);
		reservedFunds = reservedFunds.add(totalPrice);
		return true;
	}
	
	private void refundFunds(BigDecimal amount) {
		availableFunds = availableFunds.add(amount);
		reservedFunds = reservedFunds.subtract(amount);
	}
	
	private void refundStocks(StockHandle stock, int volume) {
		int oldAmount = reservedPortfolio.get(stock);
		int newAmount = oldAmount - volume;
		
		if(newAmount > 0) reservedPortfolio.put(stock, newAmount);
		else reservedPortfolio.remove(stock);
		
		oldAmount = portfolio.get(stock);
		newAmount = oldAmount + volume;
		portfolio.put(stock, newAmount);
	}

	private boolean reserveStocks(StockHandle stock, int volume) {
		if (!portfolio.containsKey(stock))
			return false; // we don't own any of this stock (for now that means
							// no trade)

		int amtOwned = portfolio.get(stock);
		if (amtOwned < volume)
			return false; // we don't own enough for this sale

		// subtract sold stock now - will be returned if we cancel?
		portfolio.put(stock, amtOwned - volume);

		int alreadyReserved = reservedPortfolio.containsKey(stock) ? reservedPortfolio
				.get(stock) : 0;
		reservedPortfolio.put(stock, alreadyReserved + volume);

		return true;
	}

	private void removeReserveStock(StockHandle stock, int volume) {
		int alreadyReserved = reservedPortfolio.containsKey(stock) ? reservedPortfolio
				.get(stock) : 0;
		alreadyReserved -= volume;
		
		if(alreadyReserved != 0) reservedPortfolio.put(stock, alreadyReserved);
		else reservedPortfolio.remove(stock);
	}

	private void addStockToPortfolio(StockHandle stock, int volume) {
		int hasAlready = portfolio.containsKey(stock) ? portfolio.get(stock)
				: 0;
		hasAlready += volume;
		portfolio.put(stock, hasAlready);
	}

	/**
	 * Called by the user to determine which stocks are available to trade.
	 * 
	 * @return An iterator over StockHandles for all stocks available in this
	 *         market
	 */
	public List<StockHandle> getAllStocks() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		List<StockHandle> res = null;
		try {
			res = dataHandler.getAllStocks(dataset);
		} catch (SQLException e) {
			tryCleanAbort();
			throw new SimulationAbortedException(e);
		}
		return res;
	}

	/**
	 * Called by the user to get the BuyOrders which have not yet been filled.
	 * @return A new iterator over buy orders which are still outstanding
	 */
	public Iterator<BuyOrder> getOutstandingBuyOrders() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		
		try {
			List<Iterator<BuyOrder>> orders = new LinkedList<>();
			for(UserOrderBook uob : booksWithPosition) {
				orders.add(uob.getMyBids());
			}
			return new MultiIterator<>(orders);
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}
	
	/**
	 * Called by the user to get the SellOrders which have not yet been filled.
	 * @return A new iterator over sell orders which are still outstanding
	 */
	public Iterator<SellOrder> getOutstandingSellOrders() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		
		try {
			List<Iterator<SellOrder>> orders = new LinkedList<>();
			for(UserOrderBook uob : booksWithPosition) {
				orders.add(uob.getMyOffers());
			}
			return new MultiIterator<>(orders);
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}

	/**
	 * Called by the user to get the user's current portfolio (stocks and
	 * amounts)
	 * 
	 * @return A new iterator over Map entries between StockHandle and amounts
	 *         of stock.
	 */
	public Iterator<Entry<StockHandle, Integer>> getPortfolio() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		return new ProtectedIterator<>(portfolio.entrySet().iterator());
	}

	/**
	 * Called by the user to get the stocks for which the user has outstanding
	 * orders.
	 * 
	 * @return A new iterator over stocks which have outstanding orders
	 */
	public Iterator<StockHandle> getStocksWithOutstanding() {
		if (threadShouldBeAborting)
			throw new SimulationAbortedException();
		
		try {
			List<StockHandle> out = new LinkedList<StockHandle>();
			for (UserOrderBook o : booksWithPosition) {
				out.add(o.handle);
			}
			return out.iterator();
		} catch (SimulationAbortedException e) {
			tryCleanAbort();
			throw e;
		}
	}

	/**
	 * Get a simple iterator over all StockHandles the user owns any of.
	 * 
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
	 * Called when the user's algorithm should terminate, to try to encourage
	 * him to.
	 */
	public void tryCleanAbort() {
		threadShouldBeAborting = true;
	}
}
