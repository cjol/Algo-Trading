package unitTests;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

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
	private BigDecimal[] intArrToBD(int[] arr) {
		BigDecimal[] result = new BigDecimal[arr.length];
		
		for (int i = 0; i < arr.length; i++) result[i] = new BigDecimal(arr[i]);
		return result;
	}
	
	@Test
	public void testOffset() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(intArrToBD(new int[]{0, 1, 2, 3, 4, 5, 6}));
		
		Offset offset = new Offset(testData, 1);

		assertTrue(offset.getValue(0).intValue() == 5);
		assertTrue(offset.getValue(3).intValue() == 2);
		assertTrue(offset.getValue(4).intValue() == 1);
		
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
				intArrToBD(new int[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 42}));
		
		//A 1-wide MA is just the identity function
		MovingAverage movingAverage1 = new MovingAverage(testData, 1);
		assertTrue(movingAverage1.getValue(0).intValue() == 42);
		assertTrue(movingAverage1.getValue(4).intValue() == 2);
		
		MovingAverage movingAverage3 = new MovingAverage(testData, 3);
		assertTrue(movingAverage3.getValue(1).intValue() == 3);
		assertTrue(movingAverage3.getValue(4).intValue() == 2);
		assertTrue(movingAverage3.getValue(8).intValue() == 1);
		
		MovingAverage movingAverage5 = new MovingAverage(testData, 5);
		//13 / 5 = 2.6
		assertTrue(movingAverage5.getValue(1).multiply(new BigDecimal(10)).intValue() == 26);
		//53 / 5 = 10.6
		assertTrue(movingAverage5.getValue(0).multiply(new BigDecimal(10)).intValue() == 106);
	}
	
	@Test
	public void testDerivative() throws TickOutOfRangeException {
		ArrayValueObject testData = new ArrayValueObject(intArrToBD(new int[]{1, 1, 2, 3, 5, 8, 13, 21, 34}));
		
		FirstDerivative derivative = new FirstDerivative(testData); //Should be 0, 1, 1, 2, 3, 5, 8, 13
		Offset offsetData = new Offset(testData, 2);
		
		for (int i = 0; i <= 6; i++) {
			assertTrue(derivative.getValue(i).equals(offsetData.getValue(i)));
		}
	}
	
	@Test
	public void testArithmetic() throws TickOutOfRangeException {
		//Tests for +, -, /, *.
		
		ArrayValueObject testData1 = new ArrayValueObject(intArrToBD(new int[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 42}));
		ArrayValueObject testData2 = new ArrayValueObject(intArrToBD(new int[]{1, 0, -1, -2, -2, -3, -4, -5, -5, -6, 32}));
		
		Addition addition = new Addition(testData1, testData1);
		assertTrue(addition.getValue(0).intValue() == 84);
		assertTrue(addition.getValue(5).intValue() == 4);
		
		Subtraction subtraction = new Subtraction(testData1, testData1);
		Constant constant = new Constant(new BigDecimal(0));
		assertTrue(subtraction.getValue(0).equals(constant.getValue(0)));
		assertTrue(subtraction.getValue(8).equals(constant.getValue(8)));
		
		Multiplication multiplication = new Multiplication(testData1, testData2);
		assertTrue(multiplication.getValue(1).intValue() == -18);
		assertTrue(multiplication.getValue(6).intValue() == -4);
		
		Division division = new Division(testData1, new Constant(new BigDecimal(2)));
		assertTrue(division.getValue(9).multiply(new BigDecimal(10)).intValue() == 5);
		assertTrue(division.getValue(1).multiply(new BigDecimal(10)).intValue() == 15);
		assertTrue(division.getValue(5).intValue() == 1);
		
	}
}
