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
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//Subtract the current from the previous value
		int curr = underlying.getValue(ticksBack);
		int prev = underlying.getValue(ticksBack + 1);
		
		return curr - prev;
	}

}
