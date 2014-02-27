package testHarness.clientConnection;

import java.io.Serializable;
import java.util.HashMap;

import config.YamlConfig;
import config.YamlParam;

/**
 * Extra options on how to run the simulation. Made visible to the user as well.
 * @author Lawrence Esswood
 *
 */
public class Options implements Serializable {
	private static final long serialVersionUID = 2L;
	public final int tickSize;
	public final int maxTicks;
	public final int startingFunds;
	public final int commissionRate; // percent of a basis point, i.e. 1/10000'th
	public final int timeout;
	
	private final HashMap<String, String> userParams;
	
	public Options(int tickSize, int maxTicks, int startingFunds, int commisionRate, int timeout) {
		this.tickSize = tickSize;
		this.maxTicks = maxTicks;
		this.startingFunds = startingFunds;
		this.commissionRate = commisionRate;
		this.timeout = timeout;
		this.userParams = null;
	}
	
	public Options(YamlConfig config) {
		this.tickSize = config.tickSize;
		this.maxTicks = config.maxTicks;
		this.startingFunds = config.startingFunds;
		this.commissionRate = config.commissionRate;
		this.timeout = config.timeout;
		this.userParams = new HashMap<>();
		
		for(YamlParam param : config.params) {
			userParams.put(param.name, param.value);
		}
		
	}
	
	public boolean hasParam(String paramName) {
		return (userParams != null) && userParams.containsKey(paramName);
	}
	
	public String getParam(String paramName) {
		return userParams.get(paramName);
	}
	
	public static final Options defaultOptions = new Options(500, Integer.MAX_VALUE, 10000, 50, 60000);
}