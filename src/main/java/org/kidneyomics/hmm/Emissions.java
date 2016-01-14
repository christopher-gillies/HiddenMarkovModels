package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

class Emissions {
	
	private static final double epsilon = 0.00001;
	private final RandomNumberService randomNumberService;
	//ideally the Symbol key will point to the same symbol objects across all states
	private final Map<Symbol,Double> symbols;

	Emissions() {
		this.randomNumberService = new DefaultRandomNumberSerivce();
		this.symbols = new HashMap<Symbol,Double>();
	}
	
	Emissions(RandomNumberService randomNumberService) {
		this.randomNumberService = randomNumberService;
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
		
		return  Math.abs(1 - sum) <= epsilon;
	}
	
	Symbol emit() {	
		return randomNumberService.emit(symbols);
	}
	
}
