package testHarness;

public interface ITradingAlgorithm {
	/**
	 * Starts the simulation of the user code.
	 * @param marketView The market view with which the user should interface.
	 */
	public void run(MarketView marketView);
}
