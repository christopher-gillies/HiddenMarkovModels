package org.kidneyomics.hmm;

public interface Nextable<T> {
	void setNext(T next);
	T getNext();
}
