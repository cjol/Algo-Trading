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
	private Thread marketViewThread;
	
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
				
				startSim(options.timeout);
				
				result = null;
				try {
					if (marketView.isFinished()) {
						result = TestRequestDescription.filterOutputs(desc, outputs);
					}
				} catch (SimulationAbortedException e) {
					// timedout and thread has aborted -- handled by next case
				}
				if (result == null) {
					result = new TestResultDescription(new Exception("Test timed out"));
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
	 * Starts market in another thread
	 * @param timeout How long to give the market before terminating.
	 */
	private void startSim(long timeout)
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
	}
	
	/**
	 * The thread for the marketView
	 * @author Lawrence Esswood
	 *
	 */
	private class TestThread extends Thread{
		final MarketView mv;
		public TestThread(MarketView mv) {
			this.mv = mv;
		}
		
		@Override
		public void run() {
			mv.startSimulation();
		};
	};
	
	
	/**
	 * Trys to cleanly abort the test, then kills it.
	 */
	@SuppressWarnings("deprecation")
	public void abortTest() {
		if(marketView != null)
		{
			marketView.tryCleanAbort(marketViewThread);
			
			// A necessary evil as we don't control user code.
			if(marketViewThread != null && marketViewThread.isAlive()) {
				marketViewThread.stop();
			}
		}
	}
	
}
