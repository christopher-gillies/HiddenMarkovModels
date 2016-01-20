package org.kidneyomics.hmm;

public interface Traverseable<T> {
	/**
	 * Set the next object in the sequence
	 * @param next
	 * 
	 */
	void setNext(T next);
	
	/**
	 * Set the previous item
	 * @param previous
	 */
	void setPrevious(T previous);
	
	
	/**
	 * 
	 * @return the next object in the sequence
	 */
	T getNext();
	
	/**
	 * get the previous item
	 * @return
	 */
	T getPrevious();
	
	/**
	 * true if there is a previous item
	 * @return
	 */
	boolean hasPrevious();
	
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
