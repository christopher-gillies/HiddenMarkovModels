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
	
	private ViterbiNode(State state) {
		this.state = state;
		this.nextNodes = new LinkedList<ViterbiNode>();
		this.previousNodes = new LinkedList<ViterbiNode>();
	}
	
	public static ViterbiNode createViterbiNodeFromState(State state) {
		if(state == null) {
			throw new IllegalArgumentException("state cannot be null");
		} else {
			return new ViterbiNode(state);
		}
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

	//should be used to build from the left to the right
	public void setTransitions(Symbol nextSymbol) {
		this.nextNodes.clear();
		
		//we there are no transitions out of last column
		if(this.getColumn().isLastColumn()) {
			//getting previous column would be really useful here
			ViterbiColumn previous = this.getColumn().getPrevious();
			for(ViterbiNode node : previous.getNodes()) {
				//add forward edge from previous node to end node
				node.getNextNodes().add(this);
				//add backward edge from this node to previous node
				this.previousNodes.add(node);
			}
		} else {
			Collection<State> nextStates = this.state.getTransitions().getKeys();
			for(State nextState : nextStates) {
		
				ViterbiNode node;
				ViterbiColumn column;
				//if is silent state then stay in the same column
				//otherwise increase column
				if(nextState.isSilentState()) {
					column = this.getColumn();
					node = column.getNode(nextState);
				} else {
					column = this.getColumn().getNext();
					node = column.getNode(nextState);
				}
				
				if(node == null) {
					node = ViterbiNode.createViterbiNodeFromState(nextState);
					column.addNode(node);
				}
				
				//set backward edge to this node
				node.getPreviousNodes().add(this);
				//set forward edge from this node to next node;
				this.nextNodes.add(node);
			}
		}
		

	}
	
	
	
	
}
