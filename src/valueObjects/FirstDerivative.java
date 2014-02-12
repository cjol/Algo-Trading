package valueObjects;

import java.math.BigDecimal;


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
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		//Subtract the current from the previous value
		BigDecimal curr = underlying.getValue(ticksBack);
		BigDecimal prev = underlying.getValue(ticksBack + 1);
		
		return curr.subtract(prev);
	}

}
