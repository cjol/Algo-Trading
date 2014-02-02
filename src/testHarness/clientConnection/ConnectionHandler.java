package testHarness.clientConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler {

	Socket socket;
	public ConnectionHandler(Socket connection) {
		this.socket = connection;
	}
	
	public TestRequestDescription getTest() throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		Object ob = in.readObject();
		if(ob instanceof TestRequestDescription) {
			return (TestRequestDescription)ob;
		} else throw new IOException("A TestRequestDescription was expected from client, " 
									+ ob.getClass().getSimpleName() + " was recieved.");
	}
	
	public void sendResults(TestResultDescription result) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(result);
	}
	
	public void close(){
		try
		{
		socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
