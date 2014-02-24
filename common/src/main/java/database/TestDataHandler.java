package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import orderBooks.BuyOrder;
import orderBooks.Match;
import orderBooks.SellOrder;

/**
 * 
 * Responds to queries for test data from the rest of the system. 
 * Responsible for communicating with underlying (PostgreSQL) database,
 * including establishing connection, whilst hiding implementation details.
 *
 */
public class TestDataHandler {
	private static final String url = "jdbc:postgresql://127.0.0.1:5432/testenv";
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
		
		DatasetHandle res = null;
		String qStart = "SELECT ts FROM order_books WHERE dataset_id=? ORDER by ts ASC LIMIT 1";
		String qEnd =	"SELECT ts FROM order_books WHERE dataset_id=? ORDER by ts DESC LIMIT 1";
		try (PreparedStatement sStart = conn.prepareStatement(qStart);
			 PreparedStatement sEnd = conn.prepareStatement(qEnd)) {
			sStart.setInt(1, datasetID);
			sEnd.setInt(1, datasetID);
			
			try (ResultSet rStart = sStart.executeQuery();
			     ResultSet rEnd = sEnd.executeQuery()) {
				Timestamp start = null;
				Timestamp end = null;
				if (rStart.next() && rEnd.next()) {
					start = rStart.getTimestamp(1);
					end = rEnd.getTimestamp(1);
					res = new DatasetHandle(datasetID, start, end);
				}
			}
		}
		
		return res;
	}
	
	
	/**
	 * Returns a list of StockHandles for all securities in the specified
	 * dataset, {@link d}. 
	 *
	 * @param 	d	Handle of the dataset.
	 * @return 		List of StockHandles.
	 * @throws SQLException
	 */
	public List<StockHandle> getAllStocks(DatasetHandle d) throws SQLException {
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
		
		return res;
	}
	
	public Pair<List<BuyOrder>, List<SellOrder>> getLastOrderSnapshot(
				StockHandle handle, Timestamp t) throws SQLException {
		final String q = "SELECT bid1_price,bid1_volume,bid2_price,bid2_volume," +
						 "bid3_price,bid3_volume,bid4_price,bid4_volume," + 
						 "bid5_price,bid5_volume,ask1_price,ask1_volume," + 
						 "ask3_price,ask3_volume,ask3_price,ask3_volume," +
						 "ask5_price,ask5_volume,ask5_price,ask5_volume " +
						 "FROM order_books " + 
						 "WHERE dataset_id=? AND ticker=? AND ts <=? " +
						 "ORDER BY ts DESC LIMIT 1";
		
		Pair<List<BuyOrder>, List<SellOrder>> res = null;
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setInt(1, handle.getDatasetID());
			s.setString(2, handle.getTicker());
			s.setTimestamp(3, t);
			
			ResultSet r = s.executeQuery();
			if (r.next()) {
				List<BuyOrder> bids = new ArrayList<BuyOrder>();
				List<SellOrder> asks = new ArrayList<SellOrder>();
				
				for (int i=0; i <5; i++) {
					int bidPrice, bidVolume;
					int askPrice, askVolume;
					
					// note SQL indices start at 1
					bidPrice = r.getInt(2*i + 1);
					bidVolume = r.getInt(2*i + 2);
					askPrice = r.getInt(2*i + 11);
					askVolume = r.getInt(2*i + 12);
					
					// if volume is 0, nothing exists at that price level
					if (bidVolume > 0) {
						BuyOrder bid = new BuyOrder(handle, bidPrice, bidVolume);
						bids.add(bid);
					}
					if (askVolume > 0) {
						SellOrder ask = new SellOrder(handle, askPrice, askVolume);
						asks.add(ask);
					}
				}
				
				res = new Pair<List<BuyOrder>,List<SellOrder>>(bids,asks);
			}
		}
		
		return res;
	}
	
	class ResultSetIterator implements Iterator<Match> {
		private static final int CHUNK_SIZE = 1024;
		private final StockHandle stock;
		private long startID;
		private final Timestamp end;
		private LinkedList<Match> results;
		
		public ResultSetIterator(StockHandle stock, 
   								  Timestamp start, Timestamp end)
   							     throws SQLException {
 			this.stock = stock;
 			this.end = end;
 			this.results = new LinkedList<Match>();
 			
 			final String q = "SELECT match_id FROM matches " +
 						      "WHERE dataset_id=? AND ticker=? " + 
 						      "AND ts >= ? AND ts < ? LIMIT 1";
 			try (PreparedStatement s = conn.prepareStatement(q)) {
				s.setInt(1, stock.getDatasetID());
				s.setString(2, stock.getTicker());
				s.setTimestamp(3, start);
				s.setTimestamp(4, end);
				
				try (ResultSet r = s.executeQuery()) {
					if (!r.next()) {
						startID = Long.MAX_VALUE;
					} else {
						startID = r.getLong(1)-1;
					}
				}
 			}
		}
		
		public void prefetch() {
			if (!results.isEmpty()) {
				return;
			}
			
			final String q = "SELECT match_id,ts,price,volume FROM matches " +
							  "WHERE dataset_id=? AND ticker=? " +
							  "AND match_id > ? AND ts < ? LIMIT ?";

			try (PreparedStatement s = conn.prepareStatement(q)) {
				s.setInt(1, stock.getDatasetID());
				s.setString(2, stock.getTicker());
				s.setLong(3, startID);
				s.setTimestamp(4, end);
				s.setInt(5, CHUNK_SIZE);

				try (ResultSet r = s.executeQuery()) {
					while (r.next()) {
						// only fetch thing after this
						startID = r.getLong(1);
						
						int price = r.getInt(3);
						int volume = r.getInt(4);

						Match newMatch = new Match(stock, price, volume);
						results.add(newMatch);
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(e); // tunnelling
			}
		}
		
		public Match next() {
			prefetch();
			
			if (results.isEmpty()) {
				throw new NoSuchElementException();
			} else {
				return results.removeFirst();
			}
		}
		
		public boolean hasNext() {
			prefetch();
			return !results.isEmpty();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * 
	 * Returns an Iterator over Match objects, representing all trades that
	 * executed between start and end. The Match objects are returned by the
	 * iterator in increasing order of time.
	 *  
	 * @param	 stock	a StockHandle representing an individual security, in a particular DataSet
	 * @param 	 start	Timestamp object specifying earliest trades to return; if null, does not impose a minimum
	 * @param 	 end	Timestamp object specifying latest trades to return; if null, does not impose a maximum
	 * @return	 iterator over specified orders
	 * @throws SQLException
	 */
	public Iterator<Match> getMatches(StockHandle stock, 
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
