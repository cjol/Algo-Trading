package valueObjects;


/** Represents a calculation on a time series that returns a time series */
public interface IValued {
	
	/**
	 * @param ticksBack How many ticks ago is the required value.
	 * @return The value of the time series ticksBack ticks ago.
	 * @throws TickOutOfRangeException in case this value is unreachable.
	 */
	public double getValue(int ticksBack) throws TickOutOfRangeException;
}
