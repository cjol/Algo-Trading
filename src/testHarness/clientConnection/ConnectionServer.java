package testHarness.clientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;
import testHarness.TestDataHandler;

public class ConnectionServer {

	private TestDataHandler dataHandler;
	private  OutputServer outputServer;
	
	private ServerSocket listenSocket;
	private Thread listenThread;
	private List<TestInstance> testInstances = new LinkedList<TestInstance>();
	
	public ConnectionServer(int port, TestDataHandler dataHandler, OutputServer outputServer) throws IOException {
		this.dataHandler = dataHandler;
		this.outputServer = outputServer;
		listenSocket = new ServerSocket(port);
	}
	
	public void startServer() throws IOException{
		
		listenThread = Thread.currentThread();
		while(!listenThread.isInterrupted())
		{
			Socket clientSocket = null;
			try {
				clientSocket = listenSocket.accept();
				ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
				TestInstance testInstance = new TestInstance(connectionHandler, dataHandler, outputServer);
				testInstances.add(testInstance);
				testInstance.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopServer() throws IOException {
		listenThread.interrupt();
		listenSocket.close();
		for(TestInstance t: testInstances) t.abortTest();
	}
}
