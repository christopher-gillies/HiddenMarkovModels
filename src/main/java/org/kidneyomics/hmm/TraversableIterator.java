package org.kidneyomics.hmm;

import java.util.Iterator;

public class TraversableIterator<T extends Traverseable<T>> implements Iterator<T> {

	T current = null;
	T next = null;
	
	TraversableIterator(T head) {
		current = head;
		next = head;
	}
	
	public boolean hasNext() {
		if(next != null) {
			return true;
		} else {
			return false;
		}
	}

	public T next() {
		current = next;
		next = current.getNext();
		return current;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	
}