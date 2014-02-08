package testHarness.clientConnection;

import java.io.Serializable;

public class OutputRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final boolean respond;
	public final boolean commitToDB;
	public final String name;
	
	public OutputRequest(boolean respond, boolean commitToDB, String name) {
		this.respond = respond;
		this.commitToDB = commitToDB;
		this.name = name;
	}
}
