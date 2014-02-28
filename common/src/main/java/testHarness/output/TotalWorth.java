package testHarness.output;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import testHarness.TickData;
import testHarness.output.result.Result;
import database.OutputServer;

public class TotalWorth extends Output {
	
	Class<?>[] dependencies = new Class<?>[] {PortfolioValue.class, AvailableFunds.class};;
	Result combinedResult = null;
	
	@Override
	public Class<?>[] dependencies() { 
		return dependencies;
	}
	
	public TotalWorth(OutputServer outputServer) {
		super(outputServer);
	}

	@Override
	public Result getResult() throws RuntimeException {
		if (combinedResult == null) {
			throw new RuntimeException("Data not yet derived!");
		}
		return combinedResult;
	}

	@Override
	public void evaluateData(TickData data) {
		// Do nothing, since this is entirely derived
	}

	@Override
	public void deriveResults(List<Output> outputs) {
		Result portfolioResult = null, fundsResult = null;
		for (Output o : outputs) {
			Class<?> outputName = o.getClass();
				if (outputName.equals(dependencies[0])) {
					// portfolioValue
					portfolioResult = o.getResult(); 
				} else if (outputName.equals(dependencies[1])) {
					// availableFunds
					fundsResult = o.getResult();
				}
		}
		if (portfolioResult != null && fundsResult != null) {
			JSONObject portObject = portfolioResult.getJsonObject();
			JSONObject fundsObject = fundsResult.getJsonObject();
			JSONObject combinedObject = new JSONObject();
			for (String portKey : JSONObject.getNames(portObject)) {
				combinedObject.put(portKey, 
						(Double)fundsObject.get(portKey) 
						+ (Double)portObject.get(portKey));
			}
			combinedResult = new Result(getSlug(), "Total Worth", combinedObject);
		} else {
			throw new RuntimeException("Output dependencies not present!");
		}
	}

}
