package valueObjects;

/**
 * A value object that is the derivative of a time series, i.e. the difference
 * between consecutive terms.
 */
public class FirstDerivative implements IValued {
	private IValued underlying;
	
	/**
	 * Creates a derivative time series (difference between consecutive terms).
	 * @param underlying The time series to take the derivative of.
	 */
	public FirstDerivative(IValued underlying) {
		this.underlying = underlying;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		//Subtract the current from the previous value
		double curr = underlying.getValue(ticksBack);
		double prev = underlying.getValue(ticksBack + 1);
		
		return curr - prev;
	}

}
