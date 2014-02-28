package testHarness;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import testHarness.clientConnection.ConnectionHandler;
import testHarness.clientConnection.Options;
import testHarness.clientConnection.TestRequestDescription;
import testHarness.clientConnection.TestResultDescription;
import testHarness.clientConnection.TestRequestDescription.LoadClassException;
import testHarness.output.Output;
import database.DatasetHandle;
import database.OutputServer;
import database.TestDataHandler;

/**
 * Runs a test instance and communicates with a client
 * @author Lawrence Esswood
 *
 */
public class TestInstance implements Runnable{	
	private ConnectionHandler connection;
	private TestDataHandler dataHandler;
	private OutputServer outputServer;
	
	private MarketView marketView;
	private TestThread marketViewThread;
	
	/**
	 * 
	 * @param connection The connection to the client.
	 * @param dataHandler The data handler to use to retrieve test data
	 * @param outputServer The server to use to store output to the database
	 */
	public TestInstance(ConnectionHandler connection, TestDataHandler dataHandler, OutputServer outputServer) {
		this.connection = connection;
		this.dataHandler = dataHandler;
		this.outputServer = outputServer;
	}

	/**
	 * Starts the test
	 */
	@Override
	public void run() {
		try
		{	
			
			TestRequestDescription desc = connection.getTest();
			TestResultDescription result;

			String datasetName = desc.datasetName;
			Options options = (desc.options == null) ? Options.defaultOptions : desc.options;
			
			DatasetHandle dataset = null;
			try {
				dataset = dataHandler.getDataset(datasetName);	
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(-1); // DB error fatal
			}
			if (dataset == null) {
				result = new TestResultDescription(new Exception("Dataset " + datasetName + " not found."));
			} else {
				List<Output> outputs = TestRequestDescription.getOutputs(desc, outputServer);
				marketView = new MarketView(TestRequestDescription.getAlgo(desc), outputs, dataHandler, dataset, options);

				for (Output o : outputs) {
					o.attachMarketView(marketView);
				}
				
				Exception outcome = startSim(options.timeout);
				
				result = null;
				if (outcome == null) { // success
					result = TestRequestDescription.filterOutputs(desc, outputs);
				} else {
					result = new TestResultDescription(outcome);	
				}
			}
											
			connection.sendResults(result);
		
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | LoadClassException io) {
			io.printStackTrace();
			try {
				connection.sendResults(new TestResultDescription(io));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			connection.close();
			//TODO notify connection server to remove this test from list
		}
		
	}
	
	/**
	 * Starts market in another thread. Returns null on success;
	 * otherwise, returns an exception which occurred in the client thread
	 * (or, possibly, a pseudo-exception on timeout if client thread does not
	 * cleanly abort.)
	 * 
	 * @param timeout How long to give the market before terminating.
	 */
	private Exception startSim(long timeout)
	{
		marketViewThread = new TestThread(marketView);
		
		marketViewThread.start();
		try
		{
			marketViewThread.join(timeout);
		} catch (InterruptedException e) {
			// ignore
		}
		if (marketViewThread.isAlive()) {
			abortTest();
		}
		
		if (marketViewThread.state == TestState.EXCEPTION) {
			return marketViewThread.exception;
		} else {
			return null;
		}
	}
	
	public void abortTest() {
		if (marketViewThread != null) {
			marketViewThread.abortTest();
		}
	}
	
	/**
	 * The thread for the marketView
	 * @author Lawrence Esswood
	 *
	 */
	enum TestState {
		NOTDEAD, // running or yet to start to run
		SUCCESSFUL, // terminated successfully
		EXCEPTION; // terminated exceptionally, possibly due to timeout
	};
	
	private class TestThread extends Thread {
		// time in milliseconds to wait for clean termination
		private static final int ABORT_TIMEOUT = 1000;
		final MarketView mv;
		volatile TestState state = TestState.NOTDEAD;
		volatile Exception exception = null;
		
		public TestThread(MarketView mv) {
			this.mv = mv;
		}
		
		@Override
		public void run() {
			try {
				mv.startSimulation();	
				this.state = TestState.SUCCESSFUL;
			} catch (Exception e) {
				// exception has propagated from MarketView; record error
				// so client can be informed
				this.state = TestState.EXCEPTION;
				this.exception = e;
				throw(e);
			}	
		};
		
		/**
		 * Trys to cleanly abort the test, then kills it.
		 */
		@SuppressWarnings("deprecation")
		public void abortTest() {
			mv.tryCleanAbort();
			try {
				this.join(ABORT_TIMEOUT);	
			} catch (InterruptedException e) {
				// ignore 
			}
			
			// A necessary evil as we don't control user code.
			if(this.isAlive()) {
				this.stop();
				this.state = TestState.EXCEPTION;
				this.exception = new SimulationAbortedException("Test timed out ");
			}
			
			// note if we don't have to uncleanly abort, then client thread
			// will have propagated exception state and exception updated
		}
	};
}