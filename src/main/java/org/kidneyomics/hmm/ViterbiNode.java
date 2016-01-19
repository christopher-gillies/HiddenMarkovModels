package org.kidneyomics.hmm;

import java.util.List;

class ViterbiNode {
	
	private final Symbol symbol;
	private final State state;
	private final int column;
	
	private List<ViterbiNode> nextNodes;
	private List<ViterbiNode> previousNodes;
	
	private double score = Double.MIN_VALUE;
	private boolean isFinished = false;
	
	public ViterbiNode(Symbol symbol, State state, int column) {
		this.symbol = symbol;
		this.state = state;
		this.column = column;
	}
	
	public boolean isNullState() {
		return this.state.isSilentState();
	}
	
	
	
	
	
}
