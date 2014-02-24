package testHarness.clientConnection;

/**
 * Extra options on how to run the simulation. Made visible to the user as well.
 * @author Lawrence Esswood
 *
 */
public class Options {
	public final int tickSize;
	public final int startingFunds;
	public final int timeout;
	
	public Options(int tickSize, int startingFunds, int timeout) {
		this.tickSize = tickSize;
		this.startingFunds = startingFunds;
		this.timeout = timeout;
	}
	
	
	public static final Options defaultOptions = new Options(500, 10000,60000);
}
