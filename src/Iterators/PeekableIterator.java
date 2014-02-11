package Iterators;

import java.util.Iterator;

/**
 * An iterator that adds a peek operator
 * @author Lawrence Esswood
 *
 * @param <T>
 */
public class PeekableIterator<T> implements Iterator<T>{

	private final Iterator<T> parent;
	T top;
	public PeekableIterator (Iterator<T> rawInterator) {
		this.parent = rawInterator;
		top = (parent.hasNext()) ? parent.next() : null;
	}
	
	@Override
	public boolean hasNext() {
		return (top != null);
	}

	@Override
	public T next() {
		T hold = top;
		top = (parent.hasNext()) ? parent.next() : null;
		return hold;
	}
	
	/**
	 * 
	 * @return The next value but does not remove it from the queue.
	 */
	public T peek() {
		return top;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Operation not supported");
	}

}
