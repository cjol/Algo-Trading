package valueObjects;

public class TwoAverage implements IValued {
	private IValued a;
	private IValued b;
	
	public TwoAverage(IValued a, IValued b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		return (a.getValue(ticksBack) + b.getValue(ticksBack)) * 0.5;
	}
	
	
}
