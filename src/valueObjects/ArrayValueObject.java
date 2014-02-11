package valueObjects;


/**
 * A wrapper around a basic array to represent it as a value object.
 */
public class ArrayValueObject implements IValued {
	private int[] array;
	
	/**
	 * Creates a time series object out of an array.
	 * @param array The array to convert into a value object. The last element of the array is considered to be the current one.
	 */
	public ArrayValueObject(int[] array) {
		this.array = array;
	}

	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//e.g. 0 ticks back for a 1-item array is okay, but not 1 tick back.
		if (ticksBack >= array.length || ticksBack < 0) throw new TickOutOfRangeException();
		
		return array[array.length - ticksBack - 1];
	}
}
