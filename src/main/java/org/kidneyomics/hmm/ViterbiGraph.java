package org.kidneyomics.hmm;


class ViterbiGraph {
	
	private final HMM hmm;
	private final TraversableOrderedSet<TraversableSymbol> emittedSymbols;
	private final TraversableOrderedSet<ViterbiColumn> columns;
	//columns
	//nodes
	
	ViterbiGraph(HMM hmm, TraversableOrderedSet<TraversableSymbol> emittedSymbols) {
		this.hmm = hmm;
		//add 2 for start column and end column
		this.columns = new TraversableOrderedSet<ViterbiColumn>();
		this.emittedSymbols = emittedSymbols;
		buildViterbiGraph();
	}
	
	//TODO: add a more formal column object to this
	//this column object will have a hash table to look up the ViterbiNodes already in this column
	//this way we will not have multiple nodes that are supposed to be in the same column
	private void buildViterbiGraph() {
		State startState = this.hmm.getStartState();
		State endState = this.hmm.getEndState();
		
		//create first column, start at -1 so that the column numbers mat the position in the emittedSymbols set
		ViterbiColumn first = ViterbiColumn.createFirstColumn();
		ViterbiNode startNode = ViterbiNode.createViterbiNodeFromState(startState);
		first.addNode(startNode);
		this.columns.add(first);
		
		int i = 0;
		for(TraversableSymbol symbol : emittedSymbols) {
			ViterbiColumn next = ViterbiColumn.createInteriorColumn(i, symbol.getSymbol());
			this.columns.add(next);			
			i++;
		}
		
		//create last column
		ViterbiColumn last = ViterbiColumn.createLastColumn(emittedSymbols.size());
		ViterbiNode endNode = ViterbiNode.createViterbiNodeFromState(endState);
		last.addNode(endNode);
		this.columns.add(last);
		
	}
	
	
	
}
