package valueObjects;

public class FirstDerivative implements IValued {
	private IValued underlying;

	public FirstDerivative(IValued underlying) {
		this.underlying = underlying;
	}
	
	@Override
	public int getValue(int ticksBack) throws TickOutOfRangeException {
		//Subtract the current from the previous value
		int curr = underlying.getValue(ticksBack);
		int prev = underlying.getValue(ticksBack + 1);
		
		return curr - prev;
	}

}
