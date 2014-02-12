package valueObjects;

import java.math.BigDecimal;

/**
 * An n-element moving average time series.
 */
public class MovingAverage implements IValued {
	private IValued underlying;
	private int windowSize;
	
	
	/**
	 * Creates a simple moving average time series.
	 * @param underlying The time series to take the moving average of
	 * @param windowSize The lookback window size of the moving average, including the current value.
	 */
	public MovingAverage(IValued underlying, int windowSize) {
		this.underlying = underlying;
		this.windowSize = windowSize;
	}
	
	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		//Sum the previous windowSize values and divide by the windowSize.
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < windowSize; i++) {
			sum = sum.add(underlying.getValue(ticksBack + i));
		}
		
		//TODO: throws ArithmeticException if the decimal expansion is infinite
		//(for window sizes that are not of the form 2^x*5^y)
		return sum.divide(new BigDecimal(windowSize));
	}

}
