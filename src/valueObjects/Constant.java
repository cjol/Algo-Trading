package valueObjects;

import java.math.BigDecimal;

/**
 * Represents a constant value object.
 */
public class Constant implements IValued {
	BigDecimal constant;
	
	/**
	 * Creates a constant value object.
	 * @param constant The constant this value object will always return.
	 */
	public Constant(BigDecimal constant) {
		this.constant = constant;
	}

	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		return constant;
	}

}
