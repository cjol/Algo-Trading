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
	private static final long serialVersionUID = 1L;
	public final int tickSize;
	public final int startingFunds;
	public final int timeout;
	
	private final HashMap<String, String> userParams;
	
	public Options(int tickSize, int startingFunds, int timeout) {
		this.tickSize = tickSize;
		this.startingFunds = startingFunds;
		this.timeout = timeout;
		this.userParams = null;
	}
	
	public Options(YamlConfig config) {
		this.tickSize = config.tickSize;
		this.startingFunds = config.startingFunds;
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
	
	public static final Options defaultOptions = new Options(500, 10000,60000);
}
