package unitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import valueObjects.ArrayValueObject;
import valueObjects.FirstDerivative;
import valueObjects.MovingAverage;
import valueObjects.Offset;
import valueObjects.TickOutOfRangeException;


public class ValueObjectTests {
	@Test
	public void testOffset() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(new int[]{0, 1, 2, 3, 4, 5, 6});
		
		Offset offset = new Offset(testData, 1);

		assertTrue(offset.getValue(0) == 5);
		assertTrue(offset.getValue(3) == 2);
		assertTrue(offset.getValue(4) == 1);
		
		boolean thrown = false;
		try {
			offset.getValue(42);
		} catch (TickOutOfRangeException e) {
			thrown = true;
		}
		
		assertTrue (thrown);
	}
	
	@Test
	public void testMovingAverage() throws TickOutOfRangeException {
		//Slightly tricky because MA returns an integer series
		ArrayValueObject testData = new ArrayValueObject(new int[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 42});
		
		//A 1-wide MA is just the identity function
		MovingAverage movingAverage1 = new MovingAverage(testData, 1);
		assertTrue(movingAverage1.getValue(0) == 42);
		assertTrue(movingAverage1.getValue(4) == 2);
		
		MovingAverage movingAverage3 = new MovingAverage(testData, 3);
		assertTrue(movingAverage3.getValue(1) == 3);
		assertTrue(movingAverage3.getValue(4) == 2);
		assertTrue(movingAverage3.getValue(8) == 1);
		
		MovingAverage movingAverage5 = new MovingAverage(testData, 5);
		assertTrue(movingAverage5.getValue(1) == 3); //13 / 5 = 2.6, rounded to 3
		assertTrue(movingAverage5.getValue(0) == 11); //54 / 5 = 10.8, rounded to 11
	}
	
	@Test
	public void testDerivative() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(new int[]{1, 1, 2, 3, 5, 8, 13, 21, 34});
		
		FirstDerivative derivative = new FirstDerivative(testData); //Should be 0, 1, 1, 2, 3, 5, 8, 13
		Offset offsetData = new Offset(testData, 2);
		
		for (int i = 0; i <= 6; i++) {
			assertTrue(derivative.getValue(i) == offsetData.getValue(i));
		}
	}
}
