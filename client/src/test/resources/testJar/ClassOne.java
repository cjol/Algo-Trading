package testJar;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;

public class ClassOne implements ITradingAlgorithm{
	private ClassTwo oooh;

	@Override
	public void run(MarketView marketView) {
		System.out.println("Sweet sweet success");
	}
}
