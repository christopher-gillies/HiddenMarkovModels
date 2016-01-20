package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class ViterbiNode {
	
	private final State state;
	private ViterbiColumn column;
	
	private final List<ViterbiNode> nextNodes;
	private final List<ViterbiNode> previousNodes;
	
	private double forward = Double.MIN_VALUE;
	private double backward = Double.MIN_VALUE;
	
	private boolean forwardFinished = false;
	private boolean backwardFinished = false;
	
	ViterbiNode(State state) {
		this.state = state;
		this.nextNodes = new LinkedList<ViterbiNode>();
		this.previousNodes = new LinkedList<ViterbiNode>();
	}
	
	public boolean isSilentState() {
		return this.state.isSilentState();
	}

	public double getForward() {
		return forward;
	}

	public void setForward(double forward) {
		this.forward = forward;
	}

	public boolean isForwardFinished() {
		return forwardFinished;
	}

	public void setForwardFinished(boolean forwardFinished) {
		this.forwardFinished = forwardFinished;
	}
	

	public double getBackward() {
		return backward;
	}

	public void setBackward(double backward) {
		this.backward = backward;
	}

	public boolean isBackwardFinished() {
		return backwardFinished;
	}

	public void setBackwardFinished(boolean backwardFinished) {
		this.backwardFinished = backwardFinished;
	}


	public State getState() {
		return state;
	}

	public ViterbiColumn getColumn() {
		return column;
	}

	public List<ViterbiNode> getNextNodes() {
		return nextNodes;
	}

	public List<ViterbiNode> getPreviousNodes() {
		return previousNodes;
	}
	
	
	public void setColumn(ViterbiColumn column) {
		this.column = column;
	}

	public List<ViterbiNode> createNextNodes(Symbol nextSymbol) {
		this.nextNodes.clear();
		
		Collection<State> nextStates = this.state.getTransitions().getKeys();
		for(State state : nextStates) {
	
			ViterbiNode newNode;
			//if is silent state then stay in the same column
			//otherwise increase column
			if(state.isSilentState()) {
				newNode = new ViterbiNode(state);
				this.getColumn().addNode(newNode);
			} else {
				newNode = new ViterbiNode(state);
				this.getColumn().getNext().addNode(newNode);
			}
			
			//set backward edge to this node
			newNode.getPreviousNodes().add(this);
			//set forward edge from this node to next node;
			this.nextNodes.add(newNode);
		}
		
		return this.nextNodes;
	}
	
	
	
	
}
