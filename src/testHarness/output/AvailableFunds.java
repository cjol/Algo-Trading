package testHarness.output;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;

import testHarness.TickData;
import testHarness.output.Output;

public class AvailableFunds extends Output{
	
	private List<BigDecimal> availableFundsList;
	
	public AvailableFunds(OutputServer outputServer) {
		super(outputServer);	
		availableFundsList = new LinkedList<BigDecimal>();
	}

	@Override
	public Serializable getOutput() {
		return (Serializable)availableFundsList;
	}

	@Override
	public void evaluateData(TickData data) {
		availableFundsList.add(data.getAvailableFunds());
		
	}

	

}