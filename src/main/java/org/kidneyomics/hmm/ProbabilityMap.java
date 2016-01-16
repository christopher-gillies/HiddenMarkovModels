package org.kidneyomics.hmm;

import java.util.Set;

public interface ProbabilityMap<T> {
	double getProbability(T t);
	void setProbability(T t, double prob);
	void remove(T t);
	Set<T> getKeys(); 
	/**
	 * 
	 * @return true if this probability map has no objects in it
	 */
	boolean isNull();
}
