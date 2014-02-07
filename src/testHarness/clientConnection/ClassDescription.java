package testHarness.clientConnection;

import java.io.Serializable;

public class ClassDescription implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final byte[] definition;
	public ClassDescription(String name, byte[] definition) {
		this.name = name;
		this.definition = definition;
	}
}
