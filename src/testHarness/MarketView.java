package testHarness;

import java.util.Iterator;
import java.util.List;

import valueObjects.Book;
import valueObjects.StockHandle;

public class MarketView {

	private ITradingAlgorithm algo;
	private List<IOutput> outputs; 
	private TestDataHandler dataHandler;
	
	public MarketView(ITradingAlgorithm algo, List<IOutput> outputs, TestDataHandler dataHandler) {
		this.algo = algo;
		this.outputs = outputs;
		this.dataHandler = dataHandler;
	}
	
	public void startSimulation() {
		//TODO might need something else
		algo.run(this);
	}
	
	public Iterator<Trade> tick() {
		return null;
		//TODO should progress time and return any matches (user matches only!)
	}
	
	public boolean isFinished() {
		return false;
		//TODO should return true iff the test has finished
	}
	
	public int getRemainingFunds() {
		return -1;
		//TODO funds.
	}
	
	public void buy(StockHandle stock, int price, int volume) {
		//TODO add to book
	}
	
	public boolean sell(StockHandle stock, int price, int volume) {
		return false;
		//TODO add to book, return false if we not have the stock
	}
	
	public Iterator<StockHandle> getAllStocks() {
		return null;
		//TODO get stocks
	}
	
	public Iterator<StockHandle> getStocksWithOutstanding() {
		return null;
		//TODO get stocks where we have buy/sell not gone through
	}
	
	public Iterator<StockHandle> getOwnedStocks() {
		return null;
		//TODO get stocks we own some of
	}
	
	
	public void tryCleanAbort(Thread runningThread) {
		//TODO
		//This method should try and cleanly release any locks held by the marketView, and ensure it does not try to take
		//out any more.
	}
}
