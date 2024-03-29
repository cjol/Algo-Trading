package valueObjects;


/**
 * Represents a multiplication of elements of two value objects
 */
public class Multiplication implements IValued {
	IValued a, b;
	
	/**
	 * Creates a per-element multiplication of two time series.
	 */
	public Multiplication(IValued a, IValued b) {
		this.a = a; this.b = b;
	}

	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		return a.getValue(ticksBack) * b.getValue(ticksBack);
	}

}
