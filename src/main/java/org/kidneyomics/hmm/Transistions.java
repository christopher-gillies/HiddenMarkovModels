package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

class Transistions {
	
	//ideally the state objects will be pointers to the other states in the model
	private final Map<State,Double> probs;
	
	Transistions() {
		this.probs = new HashMap<State,Double>();
	}
	
	
	double getProbability(State s) {
		if(!this.probs.containsKey(s)) {
			throw new IllegalArgumentException("State " + s + " not found");
		}
		return this.probs.get(s);
	}
	
	void setProbability(State s, double value) {
		this.probs.put(s, value);
	}
}
