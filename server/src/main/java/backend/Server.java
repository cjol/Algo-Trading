package backend;

import java.io.IOException;
import java.sql.SQLException;

import testHarness.clientConnection.ConnectionServer;
import database.TestDataHandler;

public class Server {
	private Server() {
		// prevent instantiation
	}
	
	public static void main(String[] args) throws IOException, SQLException {
		/*
		 * TODO:
		 * 1. Process command line arguments
		 * 2. Read configuration file?
		 * 3. Instantiate other elements
		 */
		TestDataHandler tdh = new TestDataHandler("localhost//testenv");
		ConnectionServer cs = new ConnectionServer(1234, tdh, null);
		cs.startServer();
	}
}
