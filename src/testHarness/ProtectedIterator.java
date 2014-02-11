package testHarness;

import java.util.Iterator;

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
