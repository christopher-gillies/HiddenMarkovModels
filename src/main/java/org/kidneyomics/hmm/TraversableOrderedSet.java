package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

/**
 * 
 * @author cgillies
 *
 * @param <T> that extends Nextable
 * 
 * This class has a linked list and a hashset to store items in a sequence, where each item in the sequence has a distinct hashcode
 * adding items to the set will correctly set the pointers for Nextable objects
 * 
 */
public class TraversableOrderedSet<T extends Traverseable<T>> implements Set<T> {

	private final Set<T> items = new HashSet<T>();
	private T head = null;
	private T tail = null;
	private int size = 0;
	
	public TraversableOrderedSet() {
		
	}
	
	public T getFirst() {
		return head;
	}
	
	public T getLast() {
		return this.tail;
	}
	
	public T getAt(int i) {
		
		if(i < 0 || i > size) {
			throw new IllegalArgumentException("Outside bounds of list");
		}
		
		Iterator<T> iter = this.iterator();
		int count = 0;
		T result = null;
		while(iter.hasNext()) {
			T next = iter.next();
			if(count == i) {
				result = next;
				break;
			}
			count++;
		}
		return result;
	}
	
	public boolean add(T e) {
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
			// [H] head
			// [CT] current tail
			head.setNext(tail);
			tail.setNext(e);
			tail = e;
			tail.setPrevious(head);
			// [H] --> [CT] --> [e]
			//tail = [e]
		} else {
			//size > 1
			tail.setNext(e);
			e.setPrevious(tail);
			//set e to be the current tail
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

	public boolean addAll(Collection<? extends T> collection) {
		boolean ret = true;
		for(T item : collection) {
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

	public ListIterator<T> iterator() {
		return TraversableIterator.getIteratorFromHead(this.head);
	}
	
	public ListIterator<T> headIterator() {
		return iterator();
	}
	
	public ListIterator<T> tailIterator() {
		return TraversableIterator.getIteratorFromTail(this.tail);
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
		Object[] arr = new Object[this.size];
		int i = 0;
		for(T pair : this) {
			arr[i] = pair;
			i++;
		}
		
		return arr;
	}

	public <E> E[] toArray(E[] arg0) {
		
		if(arg0.length != size) {
			throw new IllegalArgumentException("input array must be the same size as the list");
		}
		
		if(size == 0) {
			return arg0;
		}
		
		Class<?> ofArray = arg0.getClass().getComponentType();
		if(ofArray != this.head.getClass()) {
			throw new IllegalArgumentException("array must be ");
		}
		
		
		int i = 0;
		
		
		for(T pair : this) {
			
			arg0[i] = (E) pair;
			i++;
		}
		return arg0;
	}
	
	
	
	@Override
	public String toString() {
		return toString("");
		
	}
	
	public String toString(String delimiter) {
		return this.head.toString(delimiter);
	}
	
	





}
