package Iterators;

import java.util.Iterator;

/**
 * An iterator that blocks use of the remove method.
 * @author Lawrence Esswood
 *
 * @param <T>
 */
public class ProtectedIterator<T> implements Iterator<T>{

	private final Iterator<T> parent;
	public ProtectedIterator (Iterator<T> rawInterator) {
		this.parent = rawInterator;
	}
	
	@Override
	public boolean hasNext() {
		return parent.hasNext();
	}

	@Override
	public T next() {
		return parent.next();
	}

	@Override
	public void remove() {
		throw new RuntimeException("Operation not supported");
	}

}
