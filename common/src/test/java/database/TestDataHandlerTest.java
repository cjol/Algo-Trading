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
import orderBooks.Order;
import orderBooks.SellOrder;

import org.junit.Before;
import org.junit.Test;

public class TestDataHandlerTest {
	private TestDataHandler dataHandler;
	
	@Before
	public void setUp() throws Exception {
		dataHandler = new TestDataHandler();
	}

	public boolean orderEqual(Order a, Order b) {
		// Order uses default equals from Object since Order's may have same 
		// attributes but represent different trades in the market
		return (a.getStockHandle() == b.getStockHandle()) &&
				(a.getPrice() == b.getPrice()) &&
				(a.getVolume() == b.getVolume());
		
	}
	@Test
	public void test() {
		try {
			DatasetHandle dataset = dataHandler.getDataset("test");
			assertNotNull(dataset);
			assertNull(dataHandler.getDataset("foo"));
			assertNull(dataHandler.getDataset(""));
			
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
			Iterator<Order> orderIt = dataHandler.getOrders(s,
									 new Timestamp(114,0,1,0,0,0,0), 
									 new Timestamp(114,0,1,0,0,1,1000));
			List<Order> orders = new ArrayList<Order>();
			while (orderIt.hasNext()) {
				orders.add(orderIt.next());
			}
			Order[] expectedOrders = {
									   new BuyOrder(s, new Timestamp(114,0,1,0,0,0,0), 9, 200),
									   new BuyOrder(s, new Timestamp(114,0,1,0,0,0,0), 10, 100),
									   new SellOrder(s, new Timestamp(114,0,1,0,0,1,0), 11, 100)
									   /*new BuyOrder(s, new Timestamp(114,0,1,0,0,2,0), new BigDecimal(11), 10),
									   new SellOrder(s, new Timestamp(114,0,1,0,0,3,0), new BigDecimal(10), 100)*/
									  };
			assertEquals(expectedOrders.length, orders.size());
			for (int i = 0; i < expectedOrders.length; i++) {
				Order expected = expectedOrders[i];
				Order actual = orders.get(i);
				
				assert(orderEqual(expected, actual));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQL Error Occurred");
		}
	}
}
