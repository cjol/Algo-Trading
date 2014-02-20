package testHarness.clientConnection;

import java.io.Serializable;

/**
 * Describes a class file.
 * @author Lawrence Esswood
 *
 */
public class ClassDescription implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final byte[] definition;
	
	/**
	 * 
	 * @param name The name of the class.
	 * @param definition The bytes of the class file.
	 */
	public ClassDescription(String name, byte[] definition) {
		this.name = name;
		this.definition = definition;
	}
}
