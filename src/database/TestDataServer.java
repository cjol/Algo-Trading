package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import testHarness.Trade;


public class TestDataServer {
	private static final String url = "postgresql:testenv";
	Connection conn;
	
	public TestDataServer() throws SQLException {
		// TODO: Make parameters configurable
		Properties props = new Properties();
		props.setProperty("user", "alpha");
		props.setProperty("password", "");
		
		conn = DriverManager.getConnection(url, props);
	}
	
	List<SQLStockHandle> getAllStocks(DatasetHandle d) throws SQLException {
		int datasetID = d.getId();
		
		List<SQLStockHandle> res = null;
		final String q = "SELECT ticker FROM securities WHERE dataset_id='?'";
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setInt(1, datasetID);
			
			try (ResultSet r = s.executeQuery()) {
				res = new ArrayList<SQLStockHandle>();
				
				while (r.next()) {
					String ticker = r.getString(1);
					res.add(new SQLStockHandle(datasetID, ticker));
				}
			}
		}
		
		return res;
	}
	
	public DatasetHandle getDataset(String name) throws SQLException {
		int datasetID = -1;
		
		String q = "SELECT dataset_id FROM datasets WHERE name='?'";
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
		
		q = "SELECT ts FROM trades WHERE dataset_id='?' ORDER BY ts LIMIT 1";
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
	
	public List<Trade> getTrades(SQLStockHandle stock, 
								 Timestamp start, Timestamp end) 
								 throws SQLException {
		if (start == null) {
			start = new Timestamp(Long.MIN_VALUE);		
		}
		if (end == null) {
			end = new Timestamp(Long.MAX_VALUE);
		}
		
		List<Trade> res = null;
		
		final String q = "SELECT ts,bid_or_ask,price,volume FROM trades" +
						 "WHERE dataset_id='?' AND ticker='?'" +
						 "AND ts > '?' AND ts < '?'";
		try (PreparedStatement s = conn.prepareStatement(q)) {
			s.setInt(1, stock.getDatasetID());
			s.setString(2, stock.getTicker());
			s.setTimestamp(3, start);
			s.setTimestamp(4, start);
			
			
			try (ResultSet r = s.executeQuery()) {
				res = new ArrayList<Trade>();
				
				while (r.next()) {
					Timestamp ts = r.getTimestamp(1);
					String bidOrAskC = r.getString(2);
					assert (bidOrAskC.equals("A") || bidOrAskC.equals("B"));
					Trade.Type type = bidOrAskC.equals("A") ? 
									  Trade.Type.ASK : Trade.Type.BID;
					int price = r.getInt(3);
					int volume = r.getInt(4);
					
					res.add(new Trade(stock, ts, type, price, volume));
				}
			}
		} 
		
		return res;
	}
}
