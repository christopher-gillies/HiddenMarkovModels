package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

class Transistions {
	private final Map<State,Double> probs;
	
	Transistions() {
		this.probs = new HashMap<State,Double>();
	}
}
