package valueObjects;

/**
 * Calculates a rolling n-window correlation between two value objects.
 */
public class Correlation implements IValued {
	private int windowSize;
	private IValued a;
	private IValued b;
	
	public Correlation(IValued a, IValued b, int windowSize) {
		this.windowSize = windowSize;
		this.a = a;
		this.b = b;
	}
	
	@Override
	public double getValue(int ticksBack) throws TickOutOfRangeException {
		double sumA = 0.0;
		double sumB = 0.0;
		double sumAB = 0.0;	//Dot product of A and B
		double sumASq = 0.0;//Sum of squares of values in A
		double sumBSq = 0.0;//Sum of squares of values in B
		
		for (int i = 0; i < windowSize; i++) {
			int id = ticksBack + i;
			
			double aVal = a.getValue(id);
			double bVal = b.getValue(id);
			
			sumA += aVal;
			sumB += bVal;
			sumAB += aVal * bVal;
			sumASq += aVal * aVal;
			sumBSq += bVal * bVal;
		}
		
		//Numerator and denominator of the correlation coefficient
		double num = windowSize * sumAB - sumA * sumB;
		double denom = Math.sqrt((windowSize * sumASq - sumA * sumA) * (windowSize * sumBSq - sumB * sumB));
		
		return num / denom;
	}

}
