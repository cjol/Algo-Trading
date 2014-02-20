package testHarness.clientConnection;

import java.io.Serializable;

/**
 * Represents a request for a single output
 * @author Lawrence Esswood
 *
 */
public class OutputRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final boolean respond;
	public final boolean commitToDB;
	public final String name;
	
	/**
	 * 
	 * @param respond A flag indicating whether the server should send back results.
	 * @param commitToDB A flag indicating whether the server should commit the result to the database.
	 * @param name The class name of the output.
	 */
	public OutputRequest(boolean respond, boolean commitToDB, String name) {
		this.respond = respond;
		this.commitToDB = commitToDB;
		this.name = name;
	}
}
