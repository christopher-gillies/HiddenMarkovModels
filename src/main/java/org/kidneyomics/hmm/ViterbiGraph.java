package org.kidneyomics.hmm;

class ViterbiGraph {
	
	private final HMM hmm;
	private final NextableOrderedSet<NextableSymbol> emittedSymbols;
	
	//columns
	//nodes
	
	ViterbiGraph(HMM hmm, NextableOrderedSet<NextableSymbol> emittedSymbols) {
		this.hmm = hmm;
		this.emittedSymbols = emittedSymbols;
	}
	
	
	
	
	
}
