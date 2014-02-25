package testJar;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;

public class ClassOne implements ITradingAlgorithm{
	@SuppressWarnings("unused")
	private ClassTwo oooh;

	@Override
	public void run(MarketView marketView, Options options) {
		System.out.println("Sweet sweet success");
	}
}
