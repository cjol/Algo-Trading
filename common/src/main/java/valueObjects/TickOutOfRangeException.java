package valueObjects;

/**
 * Thrown when a value object can't find its value a given amount of ticks ago.
 * For example, when the amount is larger than the array length for an array
 * or when it's negative.
 */
public class TickOutOfRangeException extends Exception {
	private static final long serialVersionUID = 1L;
}
