package valueObjects;

/**
 * Represents a constant value object.
 */
public class Constant implements IValued {
	double constant;
	
	/**
	 * Creates a constant value object.
	 * @param constant The constant this value object will always return.
	 */
	public Constant(double constant) {
		this.constant = constant;
	}

	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		return constant;
	}

}
