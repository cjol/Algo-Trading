package valueObjects;

public interface IValued {
	public int getValue(int ticksBack) throws TickOutOfRangeException;
}
