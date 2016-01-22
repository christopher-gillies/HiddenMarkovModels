package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class AbstractProbabilityMap<T> implements Emitable<T>, Validatable, ProbabilityMap<T> {
	
	//ideally the state objects will be pointers to the other states in the model
	protected final Map<T,Double> probs;
	protected final RandomNumberService randomNumberService;
	protected final boolean immutable;
	
	protected AbstractProbabilityMap() {
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = new DefaultRandomNumberSerivce();
		this.immutable = false;
	}
	
	
	protected AbstractProbabilityMap(RandomNumberService randomNumberService) {
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = randomNumberService;
		this.immutable = false;
	}
	
	protected AbstractProbabilityMap(RandomNumberService randomNumberService, boolean immutable) {
		this.probs = new HashMap<T,Double>();
		this.randomNumberService = randomNumberService;
		this.immutable = immutable;
	}
	
	
	public double getProbability(T t) {
		//consider throwing exception if the state transistion is not possible
		if(this.probs.containsKey(t)) {
			return this.probs.get(t);
		} else {
			return 0.0;
		}
	}
	
	public void setProbability(T t, double value) {
		
		if(value < 0 || value > 1) {
			throw new IllegalArgumentException("value must be >=  0 and <= 1");
		}
		
		if(!immutable) {
			
			//only add if the value is greater than 0
			if(value == 0.0) {
				if(this.probs.containsKey(t)) {
					this.probs.remove(t);
				}
			} else {
				this.probs.put(t, value);
			}
		}
	}
	
	public void remove(T t) {
		if(!immutable) {
			this.probs.remove(t);
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