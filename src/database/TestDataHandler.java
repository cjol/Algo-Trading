package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import orderBookReconstructor.BuyOrder;
import orderBookReconstructor.Order;
import orderBookReconstructor.SellOrder;

/**
 * 
 * Responds to queries for test data from the rest of the system. 
 * Responsible for communicating with underlying (PostgreSQL) database,
 * including establishing connection, whilst hiding implementation details.
 *
 */
public class TestDataHandler {
	private static final String url = "postgresql://127.0.0.1:33333/testenv";
	Connection conn;
	
	/**
	 * Connects to the database. If successful, creates a new instance of
	 * TestDataServer. Throws SQLException on error; e.g. server unavailable.
	 * 
	 * @throws SQLException
	 */
	public TestDataHandler() throws SQLException {
		// TODO: Make parameters configurable
		Properties props = new Properties();
		props.setProperty("user", "alpha");
		props.setProperty("password", "");
		
		conn = DriverManager.getConnection(url, props);
	}
	
	/** 
	 * 
	 * Returns a DatasetHandle object, representing a particular set of test 
	 * data stored in the database.
	 * 
	 * The {@link name} argument should specify the name given to a data set when
	 * importing it to the database. If no matching data set is found, null
	 * is returned. 
	 * 
	 * @param	 name	human-friendly name of the data set, specified at import	
	 * @return	 		DatasetHandle representing the data set, if any; otherwise, null
	 * @throws SQLException
	 * @see getAllStocks
	 */
	public DatasetHandle getDataset(String name) throws SQLException {
		int datasetID = -1;
		
		String q = "SELECT dataset_id FROM datasets WHERE name=?";
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setString(1, name);
			
			try (ResultSet r = s.executeQuery()) {
				if (r.next()) {
					datasetID = r.getInt(1);
				} else {
					return null;
				}
			}
		}
		
		q = "SELECT ts FROM trades WHERE dataset_id=? ORDER BY ts LIMIT 1";
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setInt(1, datasetID);
			
			try (ResultSet r = s.executeQuery()) {
				Date ts = null;
				if (r.next()) {
					ts = r.getTimestamp(1);
				}
				return new DatasetHandle(datasetID, ts);
			}
		}
	}
	
	
	/**
	 * Returns a list of StockHandles for all securities in the specified
	 * dataset, {@link d}. 
	 *
	 * @param 	d	Handle of the dataset.
	 * @return 		List of StockHandles.
	 * @throws SQLException
	 */
	public Iterator<StockHandle> getAllStocks(DatasetHandle d) throws SQLException {
		int datasetID = d.getId();
		
		List<StockHandle> res = null;
		final String q = "SELECT ticker FROM securities WHERE dataset_id=?";
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setInt(1, datasetID);
			
			try (ResultSet r = s.executeQuery()) {
				res = new ArrayList<StockHandle>();
				
				while (r.next()) {
					String ticker = r.getString(1);
					res.add(new StockHandle(datasetID, ticker));
				}
			}
		}
		
		return res.iterator();
	}
	
	class ResultSetIterator implements Iterator<Order> {
		private static final int CHUNK_SIZE = 1; // for debug, increase
		private final StockHandle stock;
		private Timestamp start;
		private final Timestamp end;
		private LinkedList<Order> results;
		
		public ResultSetIterator(StockHandle stock, 
   								  Timestamp start, Timestamp end) {
 			this.stock = stock;
 			this.start = start;
 			this.end = end;
 			this.results = new LinkedList<Order>();
		}
		
		public void prefetch() {
			if (!results.isEmpty()) {
				return;
			}
			
			final String q = "SELECT ts,bid_or_ask,price,volume FROM trades " +
							  "WHERE dataset_id=? AND ticker=? " +
							  "AND ts > ? AND ts < ? LIMIT ?";

			try (PreparedStatement s = conn.prepareStatement(q)) {
				s.setInt(1, stock.getDatasetID());
				s.setString(2, stock.getTicker());
				s.setTimestamp(3, start);
				s.setTimestamp(4, end);
				s.setInt(5, CHUNK_SIZE);

				try (ResultSet r = s.executeQuery()) {
					while (r.next()) {
						Timestamp ts = r.getTimestamp(1);
						String bidOrAskC = r.getString(2);
						int price = r.getInt(3);
						int volume = r.getInt(4);

						Order newOrder = null;
						switch (bidOrAskC) {
						case "A": newOrder = new SellOrder(stock, ts, new BigDecimal(price), volume);
						break;
						case "B": newOrder = new BuyOrder(stock, ts, new BigDecimal(price), volume);
						break;
						default:  throw new AssertionError("Invalid type " + bidOrAskC + " in database.");
						}
						results.addFirst(newOrder);
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(e); // tunnelling
			}
		}
		
		public Order next() {
			prefetch();
			
			if (results.isEmpty()) {
				throw new NoSuchElementException();
			} else {
				return results.removeFirst();
			}
		}
		
		public boolean hasNext() {
			prefetch();
			return results.isEmpty();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * 
	 * Returns an Iterator over Order objects, representing all orders that
	 * were placed in the training data between {@link start} and {@link end}. 
	 * The Order objects are returned by the iterator in increasing order of
	 * timestamp. 
	 * 
	 * @param	 stock	a StockHandle representing an individual security, in a particular DataSet
	 * @param 	 start	Timestamp object specifying earliest trades to return; if null, does not impose a minimum
	 * @param 	 end	Timestamp object specifying latest trades to return; if null, does not impose a maximum
	 * @return	 iterator over specified ordders
	 * @throws SQLException
	 */
	public Iterator<Order> getOrders(StockHandle stock, 
								 Timestamp start, Timestamp end) 
								 throws SQLException {
		if (start == null) {
			start = new Timestamp(Long.MIN_VALUE);		
		}
		if (end == null) {
			end = new Timestamp(Long.MAX_VALUE);
		}
		
		ResultSetIterator rs = new ResultSetIterator(stock, start, end);
		
		return rs;
	}
}
