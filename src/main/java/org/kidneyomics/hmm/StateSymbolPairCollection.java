package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StateSymbolPairCollection implements Collection<StateSymbolPair> {

	private StateSymbolPair head = null;
	private StateSymbolPair tail = null;
	private int size = 0;
	
	public StateSymbolPairCollection() {
		
	}
	
	public boolean add(StateSymbolPair e) {
		if(e == null) {
			throw new IllegalArgumentException("Input to add cannot be null");
		}
		e.setNext(null);
		
		if(size == 0) {
			head = e;
			tail = e;
		} else if(size == 1) {
			// [H]
			// [CT]
			head.setNext(tail);
			tail.setNext(e);
			tail = e;
			
			// [H] --> [CT] --> [e]
			//tail = [e]
		} else {
			//size > 1
			tail.setNext(e);
			tail = e;
		}
		size++;
		
		/*
		 * head = 1
		 * head.next = null
		 * tail = 1
		 * tail.next = null
		 * 
		 * add 2
		 * tail.next = 2
		 * 2.next = null
		 * tail = 2
		 */
		
		return true;
	}

	public boolean addAll(Collection<? extends StateSymbolPair> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean contains(Object arg0) {
		if(head == null) {
			return false;
		}
		
		StateSymbolPair current = head;
		if(current.equals(arg0)) {
			return true;
		}
		
		while(current.getNext() != null) {
			current = current.getNext();
			if(current.equals(arg0)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<StateSymbolPair> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		return size;
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
