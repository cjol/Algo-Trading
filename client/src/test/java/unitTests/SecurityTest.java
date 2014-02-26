package unitTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.clientConnection.Options;
import testHarness.clientConnection.TestRequestDescription;
import testHarness.output.Output;
import clientLoaders.FileLoader;
import database.StockHandle;
import database.TestDataHandler;

public class SecurityTest {

	@Test
	public void test() {
		try {
			System.setProperty("java.security.policy",new File(getClass().getResource("/allperms.policy").toURI()).getPath());
		} catch (URISyntaxException e1) {
			fail("URI syntax problem (misconfigured classpath?)");
		} catch (NullPointerException e) {
			fail("could not find the policy file on the classpath");
		}
		
		SecurityManager sm = new SecurityManager();
		
		System.setSecurityManager(sm);
		
		//create a file for to see if we can access it
		File f = new File("verysecret.txt");
		f.deleteOnExit();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write("Lawrence's file, property of Lawrence, do not read, except for Lawrence");
			writer.flush();
			writer.close();
		} catch (IOException e) {fail("could not create test file");}
		
		
		TestRequestDescription req = null;
		try {
			req = FileLoader.getRequestFromFile(new File(getClass().getResource("/naughty.jar").toURI()).getPath());
		} catch (IOException e) {
			e.printStackTrace();
			fail("failed to load jar");
		} catch (URISyntaxException e) {
			fail("URI syntax problem (misconfigured classpath?)");
		} catch (NullPointerException e) {
			fail("could not find the naughty jar on the classpath");
		}
		
		ITradingAlgorithm algo = null;
		try {
			algo = TestRequestDescription.getAlgo(req);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to load classes");
		}
		
		//create a fake market view to see that we can access it
		MarketView mv = new FakeMarket(null,null,null);
		ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outStrm));
		
		//run the malicious code.
		for(int i = 0; i < 8; i++) {
			algo.run(mv, null);
		}
		//The previous (checking for true every time) didn't work in maven, since
		//the stream didn't flush every .equals("true") invocation (so it would become
		//true -> truetrue -> truetruetrue...)
		assertTrue(outStrm.toString().equals("truetruetruetruetruetruetruetrue"));
		
	}

	private static class FakeMarket extends MarketView
	{
		private static final int DUMMY_TICK_SIZE = 1000;
		private static final int DUMMY_MAX_TICKS = 1000;
		private static final int DUMMY_STARTING_FUNDS = 1000;
		private static final int DUMMY_TIMEOUT = 60;
		private static final Options DUMMY_OPTIONS = new Options(DUMMY_TICK_SIZE, DUMMY_MAX_TICKS, DUMMY_STARTING_FUNDS, DUMMY_TIMEOUT);
		
		public FakeMarket(ITradingAlgorithm algo, List<Output> outputs,
				TestDataHandler dataHandler) {
			super(algo, outputs, dataHandler,null, DUMMY_OPTIONS);
		}
		
		@Override
		public List<StockHandle> getAllStocks() {
			return null;
		}
	
	}

}
