package org.kidneyomics.hmm;

class ViterbiGraph {
	
	private final HMM hmm;
	private final NextableOrderedSet<NextableSymbol> emittedSymbols;
	
	//columns
	//nodes
	
	ViterbiGraph(HMM hmm, NextableOrderedSet<NextableSymbol> emittedSymbols) {
		this.hmm = hmm;
		this.emittedSymbols = emittedSymbols;
		buildViterbiGraph();
	}
	
	//TODO: add a more formal column object to this
	//this column object will have a hash table to look up the ViterbiNodes already in this column
	//this way we will not have multiple nodes that are supposed to be in the same column
	private void buildViterbiGraph() {
		State startState = this.hmm.getStartState();
		
		ViterbiNode startNode = new ViterbiNode(null, startState, 0);
	}
	
	
	
}
