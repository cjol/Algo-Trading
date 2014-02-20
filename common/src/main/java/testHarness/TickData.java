package testHarness;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import orderBooks.Order;
import database.StockHandle;

public class TickData {	
	private final Map<StockHandle, Integer> portfolio;
	private final List<Order> outstandingOrders;
	private final Timestamp dataTimestamp;
	private final BigDecimal availableFunds;	
	
	public TickData(Timestamp dataTimestamp, Map<StockHandle, Integer> portfolio, List<Order> outstandingOrders, BigDecimal availableFunds) {		
		this.portfolio = portfolio;
		this.outstandingOrders = outstandingOrders;
		this.dataTimestamp = dataTimestamp;
		this.availableFunds = availableFunds;
	}
	
	public Map<StockHandle, Integer> getPortfolio() {
		return portfolio;
	}

	public List<Order> getOutstandingOrders() {
		return outstandingOrders;
	}

	public Timestamp getDataTimestamp() {
		return dataTimestamp;
	}

	public BigDecimal getAvailableFunds() {
		return availableFunds;
	}
}
