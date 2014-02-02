package testHarness;

import java.util.List;

public class MarketView {

	public MarketView(ITradingAlgorithm algo, List<IOutput> outputs, TestDataHandler dataHandler) {
		
	}
	
	public void startSimulation() {
		
	}
	
	public void tryCleanAbort(Thread runningThread)
	{
		//This method should try and cleanly release any locks held by the marketView, and ensure it does not try to take
		//out any more.
	}
}
