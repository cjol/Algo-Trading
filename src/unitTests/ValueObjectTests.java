package unitTests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import valueObjects.Addition;
import valueObjects.ArrayValueObject;
import valueObjects.Constant;
import valueObjects.Division;
import valueObjects.FirstDerivative;
import valueObjects.MovingAverage;
import valueObjects.Multiplication;
import valueObjects.Offset;
import valueObjects.Subtraction;
import valueObjects.TickOutOfRangeException;


public class ValueObjectTests {
	
	@Test
	public void testOffset() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(new double[]{0, 1, 2, 3, 4, 5, 6});
		
		Offset offset = new Offset(testData, 1);
		
		//FP comparison here, should be okay
		//since we didn't perform any operations
		assertTrue(offset.getValue(0) == 5.0);
		assertTrue(offset.getValue(3) == 2.0);
		assertTrue(offset.getValue(4) == 1.0);
		
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
		ArrayValueObject testData = new ArrayValueObject(
				new double[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 42});
		
		//A 1-wide MA is just the identity function
		MovingAverage movingAverage1 = new MovingAverage(testData, 1);
		assertTrue(movingAverage1.getValue(0) == 42.0);
		assertTrue(movingAverage1.getValue(4) == 2.0);
		
		//Use results of operations that should be performed
		//so that we can expect the equality on doubles to work reasonably
		MovingAverage movingAverage3 = new MovingAverage(testData, 3);
		assertTrue(movingAverage3.getValue(1) == 9.0/3.0);
		assertTrue(movingAverage3.getValue(4) == 4.0/2.0);
		assertTrue(movingAverage3.getValue(8) == 3.0/3.0);
		
		MovingAverage movingAverage5 = new MovingAverage(testData, 5);
		//13 / 5 = 2.6
		assertTrue(movingAverage5.getValue(1) == 13.0/5.0);
		//53 / 5 = 10.6
		assertTrue(movingAverage5.getValue(0) == 53.0/5.0);
	}
	
	@Test
	public void testDerivative() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(
				new double[]{1, 1, 2, 3, 5, 8, 13, 21, 34});
		
		FirstDerivative derivative = new FirstDerivative(testData); //Should be 0, 1, 1, 2, 3, 5, 8, 13
		Offset offsetData = new Offset(testData, 2);
		
		for (int i = 0; i <= 6; i++) {
			assertTrue(derivative.getValue(i) == (offsetData.getValue(i)));
		}
	}
	
	@Test
	public void testArithmetic() throws TickOutOfRangeException {
		//Tests for +, -, /, *.
		
		ArrayValueObject testData1 = new ArrayValueObject(
				new double[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 42});
		ArrayValueObject testData2 = new ArrayValueObject(
				new double[]{1, 0, -1, -2, -2, -3, -4, -5, -5, -6, 32});
		
		Addition addition = new Addition(testData1, testData1);
		assertTrue(addition.getValue(0) == 42.0 + 42.0);
		assertTrue(addition.getValue(5) == 2.0 + 2.0);
		
		Subtraction subtraction = new Subtraction(testData1, testData1);
		Constant constant = new Constant(0);
		assertTrue(subtraction.getValue(0) == constant.getValue(0));
		assertTrue(subtraction.getValue(8) == constant.getValue(8));
		
		Multiplication multiplication = new Multiplication(testData1, testData2);
		assertTrue(multiplication.getValue(1) == 3.0 * -6.0);
		assertTrue(multiplication.getValue(6) == 2.0 * -2.0);
		
		Division division = new Division(testData1, new Constant(2.0));
		assertTrue(division.getValue(9) == 1.0/2.0);
		assertTrue(division.getValue(1) == 3.0/2.0);
		assertTrue(division.getValue(5) == 2.0/2.0);
		
	}
}
