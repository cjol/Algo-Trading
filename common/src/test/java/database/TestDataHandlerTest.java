package database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import orderBooks.BuyOrder;
import orderBooks.Match;
import orderBooks.SellOrder;

import org.junit.Before;
import org.junit.Test;

public class TestDataHandlerTest {
	private TestDataHandler dataHandler;
	
	@Before
	public void setUp() throws Exception {
		dataHandler = new TestDataHandler();
	}

	public boolean matchEqual(Match a, Match b) {
		// Order uses default equals from Object since Order's may have same 
		// attributes but represent different trades in the market
		return (a.stockHandle.equals(b.stockHandle)) &&
				(a.price == b.price) &&
				(a.quantity == b.quantity) &&
				(a.isUserBid == b.isUserBid) &&
				(a.isUserOffer == b.isUserOffer);
		
	}
	@Test
	public void test() {
		try {
			assertNull(dataHandler.getDataset("foo"));
			assertNull(dataHandler.getDataset(""));
			DatasetHandle dataset = dataHandler.getDataset("unittests");
			assertNotNull(dataset);
			assertEquals(new Timestamp(114,0,1,0,0,0,0), dataset.getStartTime());
			assertEquals(new Timestamp(114,0,1,0,0,3,0), dataset.getEndTime());
			
			
			Iterator<StockHandle> stocks = dataHandler.getAllStocks(dataset);
			List<String> tickers = new ArrayList<String>();
			StockHandle s = null;
			while (stocks.hasNext()) {
				s = stocks.next();
				tickers.add(s.getTicker());
			}
			String[] expectedTickers = {"FOO", "BAR"};
			assertEquals(Arrays.asList(expectedTickers), tickers);
			
			// test data for both stocks is identical: just check the last one
			
			// check order book snapshot
			Pair<List<BuyOrder>, List<SellOrder>> snapshot;
			// far too early
			snapshot = dataHandler.getLastOrderSnapshot(s, new Timestamp(113,0,1,0,0,0,0));
			assertNull(snapshot);
			// ever so slightly early
			snapshot = dataHandler.getLastOrderSnapshot(s, new Timestamp(113,11,31,23,59,59,0));
			assertNull(snapshot);
			// first order snapshot
			snapshot = dataHandler.getLastOrderSnapshot(s, new Timestamp(114,0,1,0,0,0,0));
			assertNotNull(snapshot);
			// interesting order snapshot
			snapshot = dataHandler.getLastOrderSnapshot(s, new Timestamp(114,0,1,0,0,2,0));
			BuyOrder[] expectedBids = {
					new BuyOrder(s, 9, 200),
					new BuyOrder(s, 10, 100)
			};
			SellOrder[] expectedAsks = {
					new SellOrder(s, 11, 90)
			};
			assertEquals(Arrays.asList(expectedBids), snapshot.getFirst());
			assertEquals(Arrays.asList(expectedAsks), snapshot.getSecond());
			
			// check matches iterator
			Iterator<Match> matchIt = dataHandler.getMatches(s,
									 new Timestamp(114,0,1,0,0,0,0), 
									 new Timestamp(114,0,1,0,0,2,1000));
			List<Match> matches = new ArrayList<Match>();
			while (matchIt.hasNext()) {
				matches.add(matchIt.next());
			}
			
			Match[] expectedMatches = {
										new Match(s, 11, 10)
									  };
			assertEquals(expectedMatches.length, matches.size());
			for (int i = 0; i < expectedMatches.length; i++) {
				Match expected = expectedMatches[i];
				Match actual = matches.get(i);
				
				assert(matchEqual(expected, actual));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQL Error Occurred");
		}
	}
}
