package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class StateSymbolPairOrderedSet implements Set<StateSymbolPair> {

	private final Set<StateSymbolPair> items = new HashSet<StateSymbolPair>();
	private StateSymbolPair head = null;
	private StateSymbolPair tail = null;
	private int size = 0;
	
	public StateSymbolPairOrderedSet() {
		
	}
	
	public StateSymbolPair getFirst() {
		return head;
	}
	
	public StateSymbolPair getLast() {
		return this.tail;
	}
	
	public boolean add(StateSymbolPair e) {
		if(e == null) {
			throw new IllegalArgumentException("Input to add cannot be null");
		}
		
		if(this.items.contains(e)) {
			return false;
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
		
		items.add(e);
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

	public boolean addAll(Collection<? extends StateSymbolPair> collection) {
		boolean ret = true;
		for(StateSymbolPair item : collection) {
			ret = ret && add(item);
		}
		return ret;
	}

	public void clear() {
		this.head = null;
		this.tail = null;
		this.size = 0;
		this.items.clear();
	}

	public boolean contains(Object arg0) {
		return this.items.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return this.items.containsAll(arg0);
	}

	public boolean isEmpty() {
		if(this.size == 0) {
			return true;
		} else { 
			return false;
		}
	}

	public Iterator<StateSymbolPair> iterator() {
		return new StateSymbolPairIterator(this.head);
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
		StateSymbolPair[] arr = new StateSymbolPair[this.size];
		int i = 0;
		for(StateSymbolPair pair : this) {
			arr[i] = pair;
			i++;
		}
		
		return arr;
	}

	public <T> T[] toArray(T[] arg0) {
		
		if(arg0.length != size) {
			throw new IllegalArgumentException("input array must be the same size as the list");
		}
		
		Class<?> ofArray = arg0.getClass().getComponentType();
		if(ofArray != this.getClass()) {
			throw new IllegalArgumentException("array must be StateSymbolPair");
		}
		
		
		int i = 0;
		for(StateSymbolPair pair : this) {
			
			arg0[i] = (T) pair;
			i++;
		}
		return arg0;
	}
	
	
	
	@Override
	public String toString() {
		return toString("");
		
	}
	
	public String toString(String delimiter) {
		StringBuilder seqSb = new StringBuilder();
		seqSb.append("Sequence:\t");
		StringBuilder stateSb = new StringBuilder();
		stateSb.append("States:\t\t");
		
		Iterator<StateSymbolPair> iter = this.iterator();
		
		while(iter.hasNext()) {
			StateSymbolPair next = iter.next();
			seqSb.append(next.getEmittedSymbol());
			stateSb.append(next.getState());
			
			if(iter.hasNext()) {
				seqSb.append(delimiter);
				stateSb.append(delimiter);
			}
			
		}
		
		seqSb.append("\n");
		seqSb.append(stateSb.toString());
		
		return seqSb.toString();
	}
	
	
	private class StateSymbolPairIterator implements Iterator<StateSymbolPair> {

		StateSymbolPair current = null;
		StateSymbolPair next = null;
		StateSymbolPairIterator(StateSymbolPair head) {
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

		public StateSymbolPair next() {
			current = next;
			next = current.getNext();
			return current;
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
		
	}


}
