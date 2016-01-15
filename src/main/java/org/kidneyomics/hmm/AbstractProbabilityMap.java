package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractProbabilityMap<T> implements Emitable<T>, Validatable, ProbabilityMap<T> {
	
	//ideally the state objects will be pointers to the other states in the model
	protected final Map<T,Double> probs;
	protected final RandomNumberService randomNumberService;
	
	protected AbstractProbabilityMap() {
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = new DefaultRandomNumberSerivce();
	}
	
	
	protected AbstractProbabilityMap(RandomNumberService randomNumberService) {
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = randomNumberService;
	}
	
	
	public double getProbability(T t) {
		if(!this.probs.containsKey(t)) {
			throw new IllegalArgumentException("Object " + t + " not found");
		}
		return this.probs.get(t);
	}
	
	public void setProbability(T t, double value) {
		this.probs.put(t, value);
	}
	
	public void remove(T t) {
		this.probs.remove(t);
	}

	public boolean isNull() {
		return probs.size() == 0;
	}
	
	/**
	 * returns true if the probabilities sum to one or if there are no objects in it.
	 */
	public boolean isValid() {
		return isNull() || randomNumberService.sumsToOne(probs);
	}


	public T emit() {
		return randomNumberService.emit(probs);
	}
}