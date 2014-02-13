package valueObjects;

/**
 * Represents a subtraction of elements of two value objects
 */
public class Subtraction implements IValued {
	IValued a, b;
	
	/**
	 * Creates a per-element subtraction of two time series (first - second).
	 */
	public Subtraction(IValued a, IValued b) {
		this.a = a; this.b = b;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		return a.getValue(ticksBack) - b.getValue(ticksBack);
	}

}
