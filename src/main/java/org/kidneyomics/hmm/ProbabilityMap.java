package org.kidneyomics.hmm;

public interface ProbabilityMap<T> {
	double getProbability(T t);
	void setProbability(T t, double prob);
	void remove(T t);
	
	/**
	 * 
	 * @return true if this probability map has no objects in it
	 */
	boolean isNull();
}
