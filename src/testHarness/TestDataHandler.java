package testHarness;

import java.util.Iterator;

import orderBookReconstructor.Order;
import valueObjects.IValued;
import database.TestDataServer;

public class TestDataHandler {

	public TestDataHandler(TestDataServer dataServer) {
		//TODO
	}
	
	public int evaluate(IValued valuedObject) {
		return 0;
		//TODO
	}
	
	public Iterator<StockHandle> getStockHandles() {
		return null;
		//TODO: Please make stock handles unique so they can be compared by reference comparison.
		//they should contain the ids used internally by the data handler (most likely an int).
	}
	
	public Iterator<Order> tick() {
		return null;
		//TODO
	}
}
