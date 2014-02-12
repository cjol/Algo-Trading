package valueObjects;

import java.math.BigDecimal;

/**
 * Represents a division of elements of two value objects
 */
public class Division implements IValued {
	IValued a, b;
	
	/**
	 * Creates a per-element division of two time series.
	 */
	public Division(IValued a, IValued b) {
		this.a = a; this.b = b;
	}

	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		return a.getValue(ticksBack).divide(b.getValue(ticksBack));
	}

}
