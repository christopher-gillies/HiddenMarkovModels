package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class AbstractProbabilityMap<T> implements Emitable<T>, Validatable, ProbabilityMap<T> {
	
	//ideally the state objects will be pointers to the other states in the model
	protected final Map<T,Double> probs;
	protected final Map<T,Double> logProbs;
	
	protected final RandomNumberService randomNumberService;
	protected final boolean immutable;
	
	protected AbstractProbabilityMap() {
		this.logProbs = new HashMap<T,Double>();
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = new DefaultRandomNumberSerivce();
		this.immutable = false;
	}
	
	
	protected AbstractProbabilityMap(RandomNumberService randomNumberService) {
		this.probs = new HashMap<T,Double>();
		this.logProbs = new HashMap<T,Double>();
		this.randomNumberService = randomNumberService;
		this.immutable = false;
	}
	
	protected AbstractProbabilityMap(RandomNumberService randomNumberService, boolean immutable) {
		this.logProbs = new HashMap<T,Double>();
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = randomNumberService;
		this.immutable = immutable;
	}
	
	/**
	 * Since probabilities are store internally on log scale
	 * we must exponentiate the value
	 */
	public double getProbability(T t) {
		//consider throwing exception if the state transition is not possible
		if(this.probs.containsKey(t)) {
			return this.probs.get(t);
		} else {
			return 0.0;
		}
	}
	
	public double getLogProbability(T t) {
		//consider throwing exception if the state transition is not possible
		if(this.logProbs.containsKey(t)) {
			return this.logProbs.get(t);
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}
	
	/**
	 * store two maps, one on log scale and one on normal scale
	 */
	public void setProbability(T t, double value) {
		
		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("value must be >=  0 and <= 1");
		}
		
		if(!immutable) {
			
			//only add if the value is greater than 0
			if(value == 0.0) {
				if(this.probs.containsKey(t)) {
					this.probs.remove(t);
					this.logProbs.remove(t);
				}
			} else {
				this.probs.put(t, value);
				this.logProbs.put(t, Math.log(value));
			}
		}
	}
	
	public void remove(T t) {
		if(!immutable) {
			this.probs.remove(t);
			this.logProbs.remove(t);
		}
	}

	public boolean isSilent() {
		return probs.size() == 0;
	}
	
	/**
	 * returns true if the probabilities sum to one or if there are no objects in it.
	 */
	public boolean isValid() {
		return isSilent() || randomNumberService.sumsToOne(probs);
	}
	
	public boolean isImmutable() {
		return this.immutable;
	}
	
	public T emit() {
		return randomNumberService.emit(probs);
	}
	
	public Set<T> getKeys() {
		return this.probs.keySet();
	}
}