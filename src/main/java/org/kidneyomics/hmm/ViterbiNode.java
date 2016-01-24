package org.kidneyomics.hmm;

import java.util.LinkedList;
import java.util.List;

class ViterbiNode {
	
	private final State state;
	private ViterbiColumn column = null;
	private VisitLevel visitLevel = VisitLevel.NOT_VISITED;
	
	private final List<ViterbiNode> nextNodes;
	private final List<ViterbiNode> previousNodes;
	
	private double forward = 0;
	private double backward = 0;
	private double viterbi = 0;
	
	private boolean forwardFinished = false;
	private boolean backwardFinished = false;
	private boolean isFinishedViterbi = false;
	
	private ViterbiNode pointer = null;
	
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

	public double getViterbi() {
		return viterbi;
	}

	public void setViterbi(double viterbi) {
		this.viterbi = viterbi;
	}

	public boolean isFinishedViterbi() {
		return isFinishedViterbi;
	}

	public void setFinishedViterbi(boolean isFinishedViterbi) {
		this.isFinishedViterbi = isFinishedViterbi;
	}

	public ViterbiNode getPointer() {
		return pointer;
	}

	public void setPointer(ViterbiNode pointer) {
		this.pointer = pointer;
	}

	
	void calculateViterbi() {
		/*
		 * find max of all previous states k
		 * v_l(i + 1) = e_l(i + 1) * max_k ( a_kl v_k(i) )
		 * 
		 *  on log scale v_l(i + 1) = log(e_l(i + 1) + max_k (  log(a_kl) + log(v_k(i))
		 *  
		 *   viterbi will be stored on log scale
		 */
		Symbol symbol = this.getColumn().getSymbol();
		State state = this.getState();
		
		//Double.MIN_VALUE is not the smallest negative value but the smallest
		double logMax = Double.NEGATIVE_INFINITY;
		ViterbiNode maxPointer = null;
		
		for(ViterbiNode previous : this.getPreviousNodes()) {
			//if the node is not finished calculate the viterbi value
			if(!previous.isFinishedViterbi()) {
				previous.calculateViterbi();
			}
			
			double logPreViterbi = previous.getViterbi();
			double logTransitionProb = previous.getState().getTransitions().getLogProbability(state);
			double logSum = logPreViterbi + logTransitionProb;
			if(logSum > logMax) {
				logMax = logSum;
				maxPointer = previous;
			}
		}
		
		//set max values
		double log_emission = state.getEmissions().getLogProbability(symbol);
		this.viterbi = log_emission + logMax;
		this.pointer = maxPointer;
		
		//set finished
		this.isFinishedViterbi = true;
	}
	
	
	
}
