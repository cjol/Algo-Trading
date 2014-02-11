package testHarness;

import java.util.Iterator;

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
	
	public T peek() {
		return top;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Operation not supported");
	}

}
