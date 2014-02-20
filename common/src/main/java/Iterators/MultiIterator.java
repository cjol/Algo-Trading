package Iterators;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author le277
 * An iterator that flattens a list of iterators
 * @param <E> The type to iterate over
 */
public class MultiIterator<E> implements Iterator<E> {

	private Iterator<E> last;
	private final List<Iterator<E>> iters;
	
	public MultiIterator(List<Iterator<E>> iters) {
		this.iters = iters;
		discard();
	}
	@Override
	public boolean hasNext() {
		return !iters.isEmpty();
	}

	@Override
	public E next() {
		last = iters.get(0);
		E n = last.next();
		discard();
		return n;
	}

	@Override
	public void remove() {
		last.remove();
	}
	
	private void discard() {
		while(!iters.isEmpty() && !iters.get(0).hasNext()) iters.remove(0);
	}

}
