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
		String user = null, pass = null, url = null;
		switch (args.length) {
		case 3:
			pass = args[2];
		case 2:
			user = args[1];
		case 1:
			url = args[0];
		case 0:
			break;
		default:
			System.err.println("Usage: server.jar [url] [user] [pass]");
			System.exit(1);
		}
		TestDataHandler tdh = new TestDataHandler(user, pass, url);
		ConnectionServer cs = new ConnectionServer(1234, tdh, null);
		cs.startServer();
	}
}
