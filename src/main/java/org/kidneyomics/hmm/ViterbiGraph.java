package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.List;

class ViterbiGraph {
	
	private final HMM hmm;
	private final NextableOrderedSet<NextableSymbol> emittedSymbols;
	private final List<ViterbiColumn> columns;
	//columns
	//nodes
	
	ViterbiGraph(HMM hmm, NextableOrderedSet<NextableSymbol> emittedSymbols) {
		this.hmm = hmm;
		//add 2 for start column and end column
		this.columns = new ArrayList<ViterbiColumn>(this.emittedSymbols.size() + 2);
		this.emittedSymbols = emittedSymbols;
		buildViterbiGraph();
	}
	
	//TODO: add a more formal column object to this
	//this column object will have a hash table to look up the ViterbiNodes already in this column
	//this way we will not have multiple nodes that are supposed to be in the same column
	private void buildViterbiGraph() {
		State startState = this.hmm.getStartState();
		State endState = this.hmm.getEndState();
		
		//create first column
		ViterbiColumn first = new ViterbiColumn(0);
		ViterbiNode startNode = new ViterbiNode(startState);
		first.addNode(startNode);
		this.columns.add(first);
		//TODO: need to add silent states to first column
		//TODO: add transitions
		
		int i = 1;
		for(NextableSymbol symbol : emittedSymbols) {
			ViterbiColumn next = new ViterbiColumn(i, symbol.getSymbol());
			this.columns.add(next);
			//add all states to column
			for(State state : this.hmm.getStates()) {
				next.addNode(new ViterbiNode(state));
			}
			
			//TODO: add transitions
			i++;
		}
		
		//create last column
		ViterbiColumn last = new ViterbiColumn(this.emittedSymbols.size() + 1);
		ViterbiNode endNode = new ViterbiNode(endState);
		last.addNode(endNode);
		this.columns.add(last);
		
	}
	
	
	
}
