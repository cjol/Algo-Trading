package unitTests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import org.junit.Test;

import testHarness.ITradingAlgorithm;
import testHarness.clientConnection.TestRequestDescription;
import clientLoaders.FileLoader;

public class ReflectionTest {

	@Test
	public void test() {
		TestRequestDescription req = null;
		try {
			req = FileLoader.getRequestFromFile(new File(getClass().getResource("/testJar.jar").toURI()).getPath());
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed to load jar");
		} catch (URISyntaxException e) {
			fail("URI syntax problem (misconfigured classpath?)");
		} catch (NullPointerException e) {
			fail("could not find the testJar on the classpath");
		}
		
		ITradingAlgorithm algo = null;
		try {
			algo = TestRequestDescription.getAlgo(req);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to load classes");
		}
		
		ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outStrm));
		algo.run(null);
		String out = outStrm.toString();
		assertTrue("Sweet sweet success\r\n".equals(out) || "Sweet sweet success\n".equals(out)); //windows.
	}
}
