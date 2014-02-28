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
		
		for (int i = 0; i < windowSize; i++) {
			double value = underlying.getValue(i + ticksBack);
			
			sum += value;
			sumsq += value * value;
		}
		
		return sumsq - sum * sum;
	}

}
