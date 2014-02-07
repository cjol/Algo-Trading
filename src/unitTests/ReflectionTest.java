package unitTests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import testHarness.ITradingAlgorithm;
import testHarness.clientConnection.TestRequestDescription;
import clientLoaders.FileLoader;

public class ReflectionTest {

	@Test
	public void test() {
		TestRequestDescription req = null;
		try {
			req = FileLoader.getRequestFromFile("testJar.jar");
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed to load jar");
		}
		
		ITradingAlgorithm algo = null;
		try {
			algo = req.getAlgo();
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to load classes");
		}
		
		ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outStrm));
		algo.run(null);
		String out = outStrm.toString();
		assertTrue("Sweet sweet success\r\n".equals(out));
	}
}
