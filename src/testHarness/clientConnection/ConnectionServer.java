package testHarness.clientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import database.OutputServer;
import database.TestDataHandler;

/**
 * A server that listens to communication from clients.
 * @author Lawrence Esswood
 *
 */
public class ConnectionServer {

	private TestDataHandler dataHandler;
	private  OutputServer outputServer;
	
	private ServerSocket listenSocket;
	private Thread listenThread;
	private List<TestInstance> testInstances = new LinkedList<TestInstance>();
	
	/**
	 * 
	 * @param port Port number to listen on.
	 * @param dataHandler The data handler that provides data to test instances.
	 * @param outputServer The output server that stores results in the database.
	 * @throws IOException
	 */
	public ConnectionServer(int port, TestDataHandler dataHandler, OutputServer outputServer) throws IOException {
		this.dataHandler = dataHandler;
		this.outputServer = outputServer;
		listenSocket = new ServerSocket(port);
	}
	
	/**
	 * Starts the server's listening.
	 * @throws IOException
	 */
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
				new Thread(testInstance).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Halts the server and all the test instances.
	 * @throws IOException
	 */
	public void stopServer() throws IOException {
		listenThread.interrupt();
		listenSocket.close();
		for(TestInstance t: testInstances) t.abortTest();
	}
}
