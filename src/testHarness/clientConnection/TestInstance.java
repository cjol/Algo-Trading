package testHarness.clientConnection;

import java.io.IOException;
import java.util.List;

import database.OutputServer;
import testHarness.IOutput;
import testHarness.MarketView;
import testHarness.TestDataHandler;

public class TestInstance implements Runnable{

	private ConnectionHandler connection;
	private TestDataHandler dataHandler;
	private OutputServer outputServer;
	
	private MarketView marketView;
	private Thread marketViewThread;
	
	public TestInstance(ConnectionHandler connection, TestDataHandler dataHandler, OutputServer outputServer) {
		this.connection = connection;
		this.dataHandler = dataHandler;
		this.outputServer = outputServer;
	}

	@Override
	public void run() {
		try
		{
		TestRequestDescription desc = connection.getTest();
		
		//TODO outputs
		List<IOutput> outputs = desc.getOutputs(outputServer);
		marketView = new MarketView(desc.getAlgo(), outputs, dataHandler);
		startSim(15000);
		
		//TODO build result message
		TestResultDescription result = new TestResultDescription();
		connection.sendResults(result);
		
		} catch (IOException | ClassNotFoundException io) {
			io.printStackTrace();
		} finally {
			connection.close();
			//TODO notify connection server to remove this test from list
		}
		
	}
	
	
	private void startSim(int timeout)
	{
		marketViewThread = new TestThread(marketView);
		
		marketViewThread.run();
		try
		{
			marketViewThread.join(timeout);
		} catch (InterruptedException e) {
			abortTest();
		}
	}
	
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
