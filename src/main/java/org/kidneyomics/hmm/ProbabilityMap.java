package org.kidneyomics.hmm;

import java.util.Set;

public interface ProbabilityMap<T> {
	double getProbability(T t);
	double getLogProbability(T t);
	void setProbability(T t, double prob);
	void setCount(T t, double count);
	double getCount(T t);
	void remove(T t);
	void initalizeAllCountsTo0();
	void initalizeAllCountsTo1();
	void setProbsFromCounts();
	Set<T> getKeys(); 
	/**
	 * 
	 * @return true if this probability map has no objects in it
	 */
	boolean isSilent();
	boolean isImmutable();
}
