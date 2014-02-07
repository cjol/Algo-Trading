package valueObjects;

public class Offset implements IValued {
	private IValued underlying;
	private int offset;
	
	public Offset(IValued underlying, int offset) {
		this.underlying = underlying;
		this.offset = offset;
	}
	
	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//Return the value offset ticks ago.
		return underlying.getValue(ticksBack + offset);
	}
}
