package org.kidneyomics.hmm;

import java.util.LinkedList;
import java.util.List;

class ViterbiNode {
	
	private final State state;
	private ViterbiColumn column = null;
	private VisitLevel visitLevel = VisitLevel.NOT_VISITED;
	
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
	
	 static ViterbiNode createViterbiNodeFromState(State state) {
		if(state == null) {
			throw new IllegalArgumentException("state cannot be null");
		} else {
			return new ViterbiNode(state);
		}
	}
	
	 boolean isSilentState() {
		return this.state.isSilentState();
	}

	 double getForward() {
		return forward;
	}

	 void setForward(double forward) {
		this.forward = forward;
	}

	 boolean isForwardFinished() {
		return forwardFinished;
	}

	 void setForwardFinished(boolean forwardFinished) {
		this.forwardFinished = forwardFinished;
	}
	

	 double getBackward() {
		return backward;
	}

	 void setBackward(double backward) {
		this.backward = backward;
	}

	 boolean isBackwardFinished() {
		return backwardFinished;
	}

	 void setBackwardFinished(boolean backwardFinished) {
		this.backwardFinished = backwardFinished;
	}


	 State getState() {
		return state;
	}

	 ViterbiColumn getColumn() {
		return column;
	}

	 List<ViterbiNode> getNextNodes() {
		return nextNodes;
	}

	 List<ViterbiNode> getPreviousNodes() {
		return previousNodes;
	}
	
	
	 void setColumn(ViterbiColumn column) {
		this.column = column;
	}

	 VisitLevel getVisitLevel() {
		return visitLevel;
	}

	 void setVisitLevel(VisitLevel visitLevel) {
		this.visitLevel = visitLevel;
	}

	
	
	
	
	
}
