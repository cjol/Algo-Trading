package testHarness.output;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

import testHarness.TickData;
import database.OutputServer;
import database.StockHandle;

public class Portfolio extends Output{
	
	private final LinkedList<Map<StockHandle, Integer>> portfolio;
	
	public Portfolio(OutputServer outputServer) {
		super(outputServer);	
		portfolio = new LinkedList<Map<StockHandle, Integer>>();
	}

	@Override
	public Serializable getOutput() {
		return (Serializable)portfolio;
	}

	@Override
	public void evaluateData(TickData data) {
		portfolio.add(data.getPortfolio());
		
	}

	

}
