package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class ViterbiNode {
	
	private final Symbol symbol;
	private final State state;
	private final int column;
	
	private final List<ViterbiNode> nextNodes;
	private final List<ViterbiNode> previousNodes;
	
	private double score = Double.MIN_VALUE;
	private boolean isFinished = false;
	
	ViterbiNode(Symbol symbol, State state, int column) {
		this.symbol = symbol;
		this.state = state;
		this.column = column;
		this.nextNodes = new LinkedList<ViterbiNode>();
		this.previousNodes = new LinkedList<ViterbiNode>();
	}
	
	public boolean isSilentState() {
		return this.state.isSilentState();
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public State getState() {
		return state;
	}

	public int getColumn() {
		return column;
	}

	public List<ViterbiNode> getNextNodes() {
		return nextNodes;
	}

	public List<ViterbiNode> getPreviousNodes() {
		return previousNodes;
	}
	
	
	public List<ViterbiNode> createNextNodes(Symbol nextSymbol) {
		this.nextNodes.clear();
		
		Collection<State> nextStates = this.state.getTransitions().getKeys();
		for(State state : nextStates) {
	
			ViterbiNode newNode;
			//if is silent state then stay in the same column
			//otherwise increase column
			if(state.isSilentState()) {
				newNode = new ViterbiNode(this.getSymbol(), state, this.getColumn());
			} else {
				newNode = new ViterbiNode(nextSymbol, state, this.getColumn() + 1);
			}
			
			//set backward edge to this node
			newNode.getPreviousNodes().add(this);
			//set forward edge from this node to next node;
			this.nextNodes.add(newNode);
		}
		
		return this.nextNodes;
	}
	
	
	
	
}
