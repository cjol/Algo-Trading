package valueObjects;

import java.math.BigDecimal;

/**
 * Offsets a value object by a certain amount of ticks.
 */
public class Offset implements IValued {
	private IValued underlying;
	private int offset;
	
	/**
	 * Creates an offset value object. All calls to this object's getValue(ticksBack)
	 * will essentially be turned into underlying.getValue(ticksBack + offset).
	 * @param underlying The value object to offset.
	 * @param offset How many ticks to offset it by.
	 */
	public Offset(IValued underlying, int offset) {
		this.underlying = underlying;
		this.offset = offset;
	}
	
	@Override
	public BigDecimal getValue(int ticksBack) throws TickOutOfRangeException {
		//Return the value offset ticks ago.
		return underlying.getValue(ticksBack + offset);
	}
}
