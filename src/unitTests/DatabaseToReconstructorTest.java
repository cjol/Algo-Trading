package unitTests;

import java.sql.SQLException;
import java.sql.Timestamp;

import orderBookReconstructor.OrderBookReconstructor;

import org.junit.Test;

import database.DatasetHandle;
import database.StockHandle;
import database.TestDataHandler;

public class DatabaseToReconstructorTest {

	@Test
	public void test() throws SQLException {
		TestDataHandler dh = new TestDataHandler();
		DatasetHandle dataset = dh.getDataset("test");
		StockHandle stockHandle = dh.getAllStocks(dataset).get(0);
		
		OrderBookReconstructor obr = new OrderBookReconstructor(
				new Timestamp(2014, 1, 1, 0, 0, 0, 0), stockHandle, dh);
		
	}

}
