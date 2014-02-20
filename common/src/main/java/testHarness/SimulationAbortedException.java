package testHarness;

/**
 * Thrown at the user when their algorithm tries to access any function after the thread has been told to abort.
 */
public class SimulationAbortedException extends RuntimeException {
	public final Exception innerException;
	
	/**
	 * 
	 * @param message
	 */
	public SimulationAbortedException(String message) {
		super(message);
		this.innerException = null;
	}
	
	/**
	 * 
	 * @param message
	 * @param inner The original exception that caused this abort.
	 */
	public SimulationAbortedException(String message, Exception inner) {
		super(message);
		this.innerException = null;
	}
	
	/**
	 * 
	 * @param inner The original exception that caused this abort.
	 */
	public SimulationAbortedException(Exception inner) {
		super(inner.getMessage());
		this.innerException = inner;
	}
	
	public SimulationAbortedException() {
		super();
		this.innerException = null;
	}
}