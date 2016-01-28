package org.kidneyomics.hmm;

import java.util.ListIterator;

public class TraversableIterator<T extends Traverseable<T>> implements ListIterator<T> {

	T current = null;
	T next = null;
	T previous = null;
	
	private TraversableIterator(T previous, T current, T next) {
		this.previous = previous;
		this.current = current;
		this.next = next;
		
	}
	
	static <E extends Traverseable<E>> TraversableIterator<E> getIteratorFromHead(E head) {
		return new TraversableIterator<E>(null, null, head);
	}
	
	static <E extends Traverseable<E>> TraversableIterator<E> getIteratorFromTail(E tail) {
		return new TraversableIterator<E>(tail, null, null);
	}
	
	
	public boolean hasNext() {
		if(next != null) {
			return true;
		} else {
			return false;
		}
	}

	public T next() {
		if(next == null) {
			previous = current;
			return null;
		} else {
			previous = current;
			current = next;
			next = current.getNext();
			return current;
		}
	}

	public void remove() {
		throw new UnsupportedOperationException("This class does not support this operation");
	}

	public boolean hasPrevious() {
		if(previous != null) {
			return true;
		} else {
			return false;
		}
	}

	public T previous() {
		if(previous == null) {
			next = current;
			return null;
		} else {
			next = current;
			current = previous;
			previous = current.getPrevious();
			return current;
		}
	}

	public int nextIndex() {
		throw new UnsupportedOperationException("This class does not support this operation");
	}

	public int previousIndex() {
		throw new UnsupportedOperationException("This class does not support this operation");
	}

	public void set(T e) {
		throw new UnsupportedOperationException("This class does not support this operation");
		
	}

	public void add(T e) {
		throw new UnsupportedOperationException("This class does not support this operation");
	}
	
	
}