package testHarness;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import orderBooks.Match;
import orderBooks.Order;
import orderBooks.UserOrderBook;
import database.StockHandle;

public class TickData {	
	public final int TICK_SIZE;
	public final Map<StockHandle,Integer> portfolio;
	public final Map<StockHandle, Integer> reservedPortfolio;
	public final	HashSet<UserOrderBook> booksWithPosition;
	public final	Timestamp currentTime;
	public final	BigDecimal availableFunds;
	public final	BigDecimal reservedFunds;
	public final	List<Match> matches;
	
	public TickData(
			int TICK_SIZE,
			Map<StockHandle,Integer> portfolio,
			Map<StockHandle, Integer> reservedPortfolio,
			HashSet<UserOrderBook> booksWithPosition,
			Timestamp currentTime,
			BigDecimal availableFunds,
			BigDecimal reservedFunds,
			List<Match> matches
			) {		
		this.TICK_SIZE = TICK_SIZE;
		this.portfolio = portfolio;
		this.reservedPortfolio = reservedPortfolio;
		this.booksWithPosition = booksWithPosition;
		this.currentTime = currentTime;
		this.availableFunds = availableFunds;
		this.reservedFunds = reservedFunds;
		this.matches = matches;
	}
}
