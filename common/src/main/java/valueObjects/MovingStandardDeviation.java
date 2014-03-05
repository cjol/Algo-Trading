package valueObjects;

/**
 * An n-element moving average time series.
 */
public class MovingStandardDeviation implements IValued {
	private IValued underlying;
	private int windowSize;
	
	
	/**
	 * Creates a simple moving standard deviation time series.
	 * @param underlying The time series to take the moving average of
	 * @param windowSize The lookback window size of the standard deviation, including the current value.
	 */
	public MovingStandardDeviation(IValued underlying, int windowSize) {
		this.underlying = underlying;
		this.windowSize = windowSize;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Return E(x^2) - E(x)^2
		
		double sum = 0.0;
		double sumsq = 0.0;
	
		//Hack to save against TickOutOfRangeExceptions: if cannot retrieve the value,
		//try the next one
		boolean first = true;
		double lastSuccessful = 0.0;

		for (int i = 0; i < windowSize; i++) {
			double value;
			try {
				value = underlying.getValue(i + ticksBack);
			} catch (TickOutOfRangeException e) {
				if (first) throw e;
				
				value = lastSuccessful;
			}
			
			sum += value;
			sumsq += value * value;
			lastSuccessful = value;
			first = false;
		}
		
		sum /= windowSize;
		sumsq /= windowSize;
		
		return Math.sqrt(sumsq - sum * sum);
	}

}
