package unitTests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import testHarness.IOutput;
import testHarness.ITradingAlgorithm;
import testHarness.MarketView;
import testHarness.StockHandle;
import testHarness.TestDataHandler;
import testHarness.clientConnection.TestRequestDescription;
import clientLoaders.FileLoader;

public class SecurityTests {

	@Test
	public void test() {
		System.setProperty("java.security.policy","allperms.policy");
		
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
			req = FileLoader.getRequestFromFile("naughty.jar");
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
		
		//create a fake market view to see that we can access it
		MarketView mv = new FakeMarket(null,null,null);
		ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outStrm));
		
		//run the malicious code.
		for(int i = 0; i < 8; i++) {
			algo.run(mv);
			assert(outStrm.equals("true"));
		}
		
	}

	private static class FakeMarket extends MarketView
	{

		public FakeMarket(ITradingAlgorithm algo, List<IOutput> outputs,
				TestDataHandler dataHandler) {
			super(algo, outputs, dataHandler);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public Iterator<StockHandle> getAllStocks() {
			return null;
		}
	
	}

}
