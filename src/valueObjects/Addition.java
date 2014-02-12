package valueObjects;

import java.math.BigDecimal;

/**
 * Represents an addition of elements of two value objects
 */
public class Addition implements IValued {
	IValued a, b;
	
	/**
	 * Creates a per-element addition of two time series.
	 */
	public Addition(IValued a, IValued b) {
		this.a = a; this.b = b;
	}

	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		return a.getValue(ticksBack).add(b.getValue(ticksBack));
	}

}
