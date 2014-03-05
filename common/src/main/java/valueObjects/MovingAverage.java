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
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Sum the previous windowSize values and divide by the windowSize.
		
		//Hack to save against TickOutOfRangeExceptions: if cannot retrieve the value,
		//try the next one
		boolean first = true;
		double lastSuccessful = 0.0;
		double sum = 0.0;
		for (int i = 0; i < windowSize; i++) {
			double val;
			try {
				val = underlying.getValue(ticksBack + i);
			} catch (TickOutOfRangeException e) {
				if (first) throw e;
				val = lastSuccessful;
			}
			
			sum += val;
			lastSuccessful = val;
			first = false;
		}
		
		return sum / (double)windowSize;
	}

}
