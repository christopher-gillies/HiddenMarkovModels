package org.kidneyomics.hmm;

public interface Nextable<T> {
	/**
	 * Set the next object in the sequence
	 * @param next
	 * 
	 */
	void setNext(T next);
	
	/**
	 * 
	 * @return the next object in the sequence
	 */
	T getNext();
	
	/**
	 * 
	 * @return true if there is another object in the sequence
	 */
	boolean hasNext();
	
	/**
	 * 
	 * @param delimter
	 * @return a string representation of the sequence
	 */
	String toString(String delimter);
}
