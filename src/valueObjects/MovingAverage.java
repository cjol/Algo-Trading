package valueObjects;

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
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//Sum the previous windowSize values and divide by the windowSize.
		int sum = 0;
		for (int i = 0; i < windowSize; i++) {
			sum += underlying.getValue(ticksBack + i);
		}
		
		//TODO: we discard the fractional part here, losing precision
		return (Math.round(sum / (float)windowSize));
	}

}
