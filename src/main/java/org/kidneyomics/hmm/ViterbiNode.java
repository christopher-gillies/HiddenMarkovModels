package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
	private boolean viterbiFinished = false;
	
	private ViterbiNode viterbiBackPointer = null;
	
	private static DoubleDescendingComparator comparator = new DoubleDescendingComparator();
	
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
		return viterbiFinished;
	}

	public void setFinishedViterbi(boolean isFinishedViterbi) {
		this.viterbiFinished = isFinishedViterbi;
	}

	public ViterbiNode getViterbiBackPointer() {
		return viterbiBackPointer;
	}

	public void setViterbiBackPointer(ViterbiNode pointer) {
		this.viterbiBackPointer = pointer;
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
		
		if(state.isEndState()) {
			//there may be a transition if this is a connected end state
			//there may be an emitted symbol
			for(ViterbiNode previous : this.getPreviousNodes()) {
				//if the node is not finished calculate the viterbi value
				if(!previous.isFinishedViterbi()) {
					previous.calculateViterbi();
				}
			
				double logPreViterbi = previous.getViterbi();
				double logTransitionProb = 0;
				if(state.isConnectedEndState()) {
					logTransitionProb = previous.getState().getTransitions().getLogProbability(state);
				}
				double logSum = logPreViterbi + logTransitionProb;
				if(logSum > logMax) {
					logMax = logSum;
					maxPointer = previous;
				}
			}
			
		} else {
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
			
		}
			
		//set max values
		if(state.isSilentState()) {
			//no emission
			this.viterbi = logMax;
		} else {
			double log_emission = state.getEmissions().getLogProbability(symbol);
			this.viterbi = log_emission + logMax;
		}
		this.viterbiBackPointer = maxPointer;
		
		//set finished
		this.viterbiFinished = true;
	}
	
	void calculateForward() {
		/*
		 * find sum of all previous states k
		 * f_l(i) = e_l(i) * sum_k ( f_k(i-1) a_kl  )
		 * 
		 *  on log scale f_l(i) = log(e_l(i)) + log( sum_k (f_k(i-1) a_kl ))
		 *  
		 *   https://en.wikipedia.org/wiki/List_of_logarithmic_identities#Summation.2Fsubtraction
		 *   
		 *   log f_l(i)  = log(e_l(i + 1)) + log (fi(i-1)a_1l) + log(1 + sum_k=2 (  exp( log(f_k(i-1)a_kl) - log (f_i(i-1)a_1l)) )
		 *   
		 *   sort log(f_k(i-1)a_kl) in descending order (largest to smallest)
		 *   
		 *   log(f_k(i-1)a_kl) = log(f_k(i-1)) + log(a_kl)
		 *  
		 *   use computeLogOfSumLogs(List<Double> logs)
		 *   
		 *   make list of all f_k(i - 1)*a_kl = log(f_k(i-1)) + log(a_kl) ... call this list logs
		 *   
		 *   log(f_l) = log(e_l(i)) + computeLogOfSumLogs(List<Double> logs)
		 */
		Symbol symbol = this.getColumn().getSymbol();
		State state = this.getState();
		double sum = 0;
		if(state.isEndState()) {
			
			//there may be a transition if this is a connected end state
			//there may be an emitted symbol
			List<Double> logFPrevAkl = new LinkedList<Double>();
			
			for(ViterbiNode previous : this.getPreviousNodes()) {
				//if the node is not finished calculate the forward value
				if(!previous.isForwardFinished()) {
					previous.calculateForward();
				}
				
				double logPreForward = previous.getForward();
				double logTransition = 0;
				if(state.isConnectedEndState()) {
					logTransition = previous.getState().getTransitions().getLogProbability(state);
				}
				double logProd = logPreForward + logTransition;
				logFPrevAkl.add(logProd);
			}
			
			sum = computeLogOfSumLogs(logFPrevAkl);

			
		} else {
			List<Double> logFPrevAkl = new LinkedList<Double>();
			
			for(ViterbiNode previous : this.getPreviousNodes()) {
				//if the node is not finished calculate the forward value
				if(!previous.isForwardFinished()) {
					previous.calculateForward();
				}
				
				double logPreForward = previous.getForward();
				double logTransition = previous.getState().getTransitions().getLogProbability(state);
				double logProd = logPreForward + logTransition;
				logFPrevAkl.add(logProd);
			}
			
			sum = computeLogOfSumLogs(logFPrevAkl);
		}
	
		if(this.isSilentState()) {
			this.forward = sum;
		} else {
			double logEmission = state.getEmissions().getLogProbability(symbol);
			this.forward = logEmission + sum;
		}
		
		this.forwardFinished = true;
	}
	
	void calculateBackward() {
		/*
		 * b_k(L) = a_k0
		 * b_k(i) = sum_l a_kl * e_l(x_{i+1}) b_i(i + 1)
		 * log b_k(i) = log (  sum_l a_kl * e_l(x_{i+1}) b_i(i + 1) ) 
		 */
		
		State state = this.getState();
		
		List<Double> logBNextAklEmission = new LinkedList<Double>();
		if(!state.isEndState()) {			
			for(ViterbiNode next : this.getNextNodes()) {
				//if the node is not finished calculate the forward value
				if(!next.isBackwardFinished()) {
					next.calculateForward();
				}
				double logTransitionProbNext = 0;
				double logEmissionProbNext = 0;
				double logBackwardNext = 0;
				
				State nextState = next.getState();
				Symbol nextSymbol = next.getColumn().getSymbol();
				if(!nextState.isEndState() || nextState.isConnectedEndState()) {
					logTransitionProbNext = state.getTransitions().getLogProbability(nextState);
					logBackwardNext = next.getBackward();
				}
				// if it is an end state that is connected assume transition probability of 1 or log (1) = 0
				
				if(!nextState.isSilentState()) {
					logEmissionProbNext = nextState.getEmissions().getLogProbability(nextSymbol);
				}
				
				double logProd = logTransitionProbNext + logEmissionProbNext + logBackwardNext;
				
				logBNextAklEmission.add(logProd);
			}
		}
		
		if(state.isEndState()) {
			this.backward = 0;
		} else {
			this.backward = computeLogOfSumLogs(logBNextAklEmission);
		}
		this.backwardFinished = true;
	}
	
	static double computeLogOfSum(List<Double> vals) {
		List<Double> logs = new ArrayList<Double>(vals.size());
		for(Double val : vals) {
			logs.add(Math.log(val));
		}
		
		double res = computeLogOfSumLogs(logs);
		return res;
	}
	
	static double computeLogOfSumLogs(List<Double> logs) {
		
		// log sum (a_i) = log a_0 + log( 1 + sum( exp( log(a_i) - log(a_0)))
		
		/*
		 * sort in descending order
		 */
		
		Collections.sort(logs, comparator);
		
		Iterator<Double> iter = logs.iterator();
		
		double largest = iter.next();
		double sum = 0;
		while(iter.hasNext()) {
			double next = iter.next();
			sum = sum + Math.exp(next - largest);
		}
		
		return largest + Math.log(1 + sum);
		
	}
		
}
