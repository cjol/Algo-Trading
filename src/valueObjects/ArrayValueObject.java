package valueObjects;

/*
 * A wrapper around a basic array to represent it as a value object.
 */
public class ArrayValueObject implements IValued {
	private int[] array;
	
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
