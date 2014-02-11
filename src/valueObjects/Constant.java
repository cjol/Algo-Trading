package valueObjects;

/**
 * Represents a constant value object.
 */
public class Constant implements IValued {
	int constant;
	
	/**
	 * Creates a constant value object.
	 * @param constant The constant this value object will always return.
	 */
	public Constant(int constant) {
		this.constant = constant;
	}

	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		return constant;
	}

}
