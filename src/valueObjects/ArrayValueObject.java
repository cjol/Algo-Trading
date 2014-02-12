package valueObjects;

import java.math.BigDecimal;


/**
 * A wrapper around a basic array to represent it as a value object.
 */
public class ArrayValueObject implements IValued {
	private BigDecimal[] array;
	
	/**
	 * Creates a time series object out of an array.
	 * @param array The array to convert into a value object. The last element of the array is considered to be the current one.
	 */
	public ArrayValueObject(BigDecimal[] array) {
		this.array = array;
	}

	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		//e.g. 0 ticks back for a 1-item array is okay, but not 1 tick back.
		if (ticksBack >= array.length || ticksBack < 0) throw new TickOutOfRangeException();
		
		return array[array.length - ticksBack - 1];
	}
}
