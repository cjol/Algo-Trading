package testHarness.output;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import testHarness.TickData;
import database.OutputServer;
import database.StockHandle;

public class Portfolio extends Output{
	
	private List<Map<StockHandle, Integer>> portfolioList;
	
	public Portfolio(OutputServer outputServer) {
		super(outputServer);	
		portfolioList = new LinkedList<Map<StockHandle, Integer>>();
	}

	@Override
	public Result getResult() {
		ListDataResult result = new ListDataResult(portfolioList);
		if(outputServer != null) outputServer.store(result);
		return result;
	}

	@Override
	public void evaluateData(TickData data) {
		portfolioList.add(data.getPortfolio());
		
	}

	

}
