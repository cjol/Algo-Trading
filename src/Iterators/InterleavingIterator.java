package Iterators;

import java.util.Comparator;
import java.util.Iterator;

/**
 * An iterator that takes two iterators and interleaves their contents together by a specified ordering
 * @author Lawrence Esswood
 *
 * @param <T> The type to iterate over
 */
public class InterleavingIterator<T> implements Iterator<T>{

	private final PeekableIterator<T> a;
	private final PeekableIterator<T> b;
	private final Comparator<T> comp;
	
	public InterleavingIterator (PeekableIterator<T> a, PeekableIterator<T> b, Comparator<T> comp) {
		this.a = a;
		this.b = b;
		this.comp = comp;
	}
	
	@Override
	public boolean hasNext() {
		return a.hasNext() || b.hasNext();
	}

	@Override
	public T next() {
		if(a.hasNext()) {
			if(b.hasNext()) {
				if(comp.compare(a.peek(), b.peek()) > 0)
					return a.next();
				return b.next();
			}
			return a.next();
		}
		return b.next();
	}

	@Override
	public void remove() {
		throw new RuntimeException("Operation not supported");
	}

}
