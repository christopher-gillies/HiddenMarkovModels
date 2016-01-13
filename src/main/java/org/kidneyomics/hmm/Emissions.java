package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

class Emissions {
	final Map<Symbol,Double> symbols;

	Emissions() {
		this.symbols = new HashMap<Symbol,Double>();
	}
	
	void addSymbol(Symbol symbol, double prob) {
		symbols.put(symbol, prob);
	}
	
	void removeSymbol(Symbol symbol) {
		this.symbols.remove(symbol);
	}
	
	double getProbability(Symbol symbol) {
		return this.symbols.get(symbol);
	}
	
	/**
	 * 
	 * @return true if the sum of the probabilities of the emission symbols is one
	 */
	boolean isValid() {
		double sum = 0;
		for(Map.Entry<Symbol, Double> entry : symbols.entrySet()) {
			sum += entry.getValue();
		}
		
		return  Math.abs(1 - sum) <= 0.0001;
	}
	
}
