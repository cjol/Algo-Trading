package backend;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;

import testHarness.clientConnection.ConnectionServer;
import database.TestDataHandler;

public class Server {
	private Server() {
		// prevent instantiation
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		TestDataHandler tdh = new TestDataHandler();
		ConnectionServer cs = new ConnectionServer(1234, tdh, null);
		cs.startServer();
	}
}
