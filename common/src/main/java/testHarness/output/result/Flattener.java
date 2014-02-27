package testHarness.output.result;

public interface Flattener<T> {
	T flatten(T current, T next, int index);
}
