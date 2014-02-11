package valueObjects;

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
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//TODO: change int to BigDecimal (for all objects?)
		return (int)(a.getValue(ticksBack) / b.getValue(ticksBack));
	}

}
