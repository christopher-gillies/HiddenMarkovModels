package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class HMM implements Validatable {
	
	private final State startState;
	private final State endState;
	private final HashMap<String,State> states;
	private final HashMap<String,Symbol> symbols;
	
	private HMM(State startState) {
		this.startState = startState;
		this.states = new HashMap<String,State>();
		this.symbols = new HashMap<String,Symbol>();
		discoverStatesAndSymbols(this.startState);
		
		State endStateTmp = null;
		//check and see if there is an explicit end state
		for(State s : states.values()) {
			if(s.isEndState()) {
				endStateTmp = s;
				break;
			}
		}
		if(endStateTmp == null) {
			this.endState = State.createEndState();
		} else {
			this.endState = endStateTmp;
			this.states.remove(endState.getName());
		}
	}
	
	
	public static HMM createHMMFromStartState(State startState) {
		if(!startState.isStartState()) {
			throw new IllegalArgumentException("Please input a start state");
		}
		return new HMM(startState);
	}
	
	
	/**
	 * 
	 * @param symbols - the symbols to decode
	 * @return the most likely state symbol sequence
	 * Perform viterbi algorithm
	 * we want max p(pi | x) 
	 * p( pi | x) = p(pi,x) / p(x) 
	 * but this would require calculating p(x)
	 * however we can maximize p(pi,x) which is equivalent to maxizing p(pi | x)
	 * since p(x) in denominator is constant for all pi  p(pi,x) / p(x) 
	 * argmax_pi (  p(pi,x) )
	 */
	public TraversableOrderedSet<StateSymbolPair> decode(List<Symbol> symbols) {
		/*
		 * create traversable ordered set
		 */
		TraversableOrderedSet<TraversableSymbol> emittedSymbols = new TraversableOrderedSet<TraversableSymbol>();
		
		for(Symbol symbol : symbols) {
			emittedSymbols.add(new TraversableSymbol(symbol));
		}
		
		/*
		 * Initialization
		 */
		ViterbiGraph graph = ViterbiGraph.createViterbiGraphFromHmmAndEmittedSymbols(this, emittedSymbols);
		
		TraversableOrderedSet<ViterbiColumn> columns = graph.getColumns();
		Iterator<ViterbiColumn> iter = columns.iterator();
		
		//first
		ViterbiColumn first = iter.next();
		ViterbiNode startNode = first.getNode(this.startState);
		//on log scale so we need to set this to 0 b/c log(1) = 0
		startNode.setViterbi(0);
		startNode.setFinishedViterbi(true);
		
		
		//others
		while(iter.hasNext()) {
			ViterbiColumn next = iter.next();
			for(ViterbiNode node : next.getNodes()) {
				// log(0) = -Inf
				node.setViterbi(Double.NEGATIVE_INFINITY);
			}
		}
		
		/*
		 * Recursion
		 */
		
		//recreate iterator
		iter = columns.iterator();
		while(iter.hasNext()) {
			ViterbiColumn next = iter.next();
			for(ViterbiNode node : next.getNodes()) {
				if(!node.isFinishedViterbi()) {
					node.calculateViterbi();
				}
			}
		}
		
		/*
		 * Termination built into calculateViterbi 
		 */
		
		/*
		 * Traceback
		 */
		LinkedList<StateSymbolPair> tmpResult = new LinkedList<StateSymbolPair>();
		ViterbiNode current = graph.getEndNode();
		while(current != null) {
			State state = current.getState();
			Symbol symbol = null;
			//only add to result if it is not a start state or not end state
			if(state.isInteriorState()){
				
				//if it is a silent state do not add the symbol associated with the column
				if(!state.isSilentState()) {
					symbol = current.getColumn().getSymbol();
				}
				StateSymbolPair pair = new StateSymbolPair(state, symbol);
				// add at the beginning of the list to ensure the correct order
				// because we are starting from the last column
				tmpResult.addFirst(pair);
			}
			//got to previous
			current = current.getViterbiBackPointer();
		}
		
		TraversableOrderedSet<StateSymbolPair> result = new TraversableOrderedSet<StateSymbolPair>();
		result.addAll(tmpResult);
		
		return result;
	}
	
	public State getEndState() {
		return this.endState;
	}
	
	public State getStartState() {
		return this.startState;
	}
	
	/**
	 * 
	 * @param n
	 * @return returns the first element in the sequence
	 */
	public TraversableOrderedSet<StateSymbolPair> generateSequence(int n) {

		
		TraversableOrderedSet<StateSymbolPair> orderedSet = new TraversableOrderedSet<StateSymbolPair>();
		//set the current state to be the start state
		State current = startState;
		
		//generate a sequence of length n by getting the next state and generating a symbol from it
		//then storing the result in the sequence result
		for(int i = 0; i < n; i++) {
			
			current = current.emitNextState();
			Symbol emittedSymbol = current.emitSymbol();
						
			StateSymbolPair pair = new StateSymbolPair(current, emittedSymbol);
			orderedSet.add(pair);
		}
		
		return orderedSet;
	}
	
	public double calcProbOfSymbolGivenStateProbs(Symbol symbol, Map<State,Double> map) {
		if(symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		/*
		 * Marginalize the joint distribution of p(symbol,state)
		 * 
		 * --> conditional probability <--
		 * p(symbol,state) = p(symbol|state) * p(state);
		 * sum_{states} p(symbol,state) = p(symbol) by the law of total probability
		 * 
		 */
		double prob = 0;
		for(Map.Entry<State, Double> entry : map.entrySet()) {
			if(entry.getKey() == null) {
				throw new IllegalArgumentException("Key cannot be null in prob map");
			}
			
			if(entry.getValue() == null) {
				throw new IllegalArgumentException("Value cannot be null in prob map");
			}
			
			double probOfState = entry.getValue();
			double probOfSymbol = entry.getKey().getEmissions().getProbability(symbol);
			prob += probOfSymbol * probOfState;
		}
		return prob;
	}
	
	/**
	 * 
	 * @param sequence
	 * @param log return in log scale
	 * @return probability of observing the sequence and the states
	 */
	public double calculateJointProbabilityOfSequencesAndStates(TraversableOrderedSet<StateSymbolPair> sequence, boolean log) {
		
		double res = 0.0;
		if(sequence.size() == 0) {
			return 0.0;
		}
		
		//compute the prob(x,pi) = a_0k * [e_k(b) * akl * e_l(b) ...]
		//log prob(x,pi) = log(a_0k) + log( e_k(b) )  +log( akl + log( e_l(b)) ...]
		
		//compute transition probability into first state
		State firstSate = sequence.getFirst().getState();
		double startProb = this.startState.getTransitions().getLogProbability(firstSate);
		res = startProb;

		//compute the emission probability for the state
		//compute the transition probability to next state if not null
		for(StateSymbolPair pair : sequence) {
			Symbol symbol = pair.getEmittedSymbol();
			State state = pair.getState();
			
			//if symbol is null then this is a null state 
			// that is there are no emissions so do not calculate the prob of emitting a symbol
			if(symbol != null) {
				double emitProb = state.getEmissions().getLogProbability(symbol);
				res = res + emitProb;
			}
			
			if(pair.getNext() != null) {
				State nextState = pair.getNext().getState();
				double transitionProb = state.getTransitions().getLogProbability(nextState);
				res = res + transitionProb;
			}
		}
		

		if(log) {
			return res;
		} else {
			return Math.exp(res);
		}
		
	}
	
	/*
	 * Perform breadth first search
	 */
	private void discoverStatesAndSymbols(State state) {
		Queue<State> queue = new LinkedList<State>();
		
		state.setVisitLevel(VisitLevel.VISITED);
		queue.add(state);
		
		while(queue.peek() != null) {
			State next = queue.poll();
			if(!next.isStartState()) {
				//Store state
				//first check if we have already seen this state
				if(this.states.containsKey(next.getName())) {
					//check and see if this state is the has the same reference as the
					//one already in the hash
					//if it is different then someone is trying to have two states with the same name
					//this case is not allowed
					State stored = this.states.get(next.getName());
					if(stored != next) {
						throw new RuntimeException("There are two states with the same name: " + stored.getName());
					}
				} else {
					//add the state b/c we have not seen it yet
					this.states.put(next.getName(), next);
				}
				
				//Store symbols
				for(Symbol symbol : next.getEmissions().getKeys()) {
					if(this.symbols.containsKey(symbol.getName())) {
						//check and see if this symbol is the has the same reference as the
						//one already in the hash
						//if it is different then someone is trying to have two Symbols with the same name
						//this case is not allowed
						
						Symbol stored = this.symbols.get(symbol.getName());
						//check object reference
						if(stored != symbol) {
							throw new RuntimeException("There are two symbols with the same name: " + stored.getName());
						}
						
					} else {
						//add symbol if we did not find it
						this.symbols.put(symbol.getName(), symbol);
					}
				}
			}
			
			//add states to the queue if this node is not already closed
			if(next.getVisitLevel() != VisitLevel.CLOSED) {
				for(State s : next.getTransitions().getKeys()) {
					if(s.getVisitLevel() == VisitLevel.NOT_VISITED) {
						s.setVisitLevel(VisitLevel.VISITED);
						queue.add(s);
					}
				}
			}
			
			//close this state b/c all reachable nodes have been added to the queue
			next.setVisitLevel(VisitLevel.CLOSED);
		}
		
	}
	
	public State getStateByName(String name) {
		return this.states.get(name);
	}
	
	public Set<String> getStateNames() {
		return this.states.keySet();
	}
	
	public Set<String> getSymbolNames() {
		return this.symbols.keySet();
	}
	
	public Symbol getSymbolByName(String name) {
		return this.symbols.get(name);
	}
	
	public Collection<State> getStates() {
		return this.states.values();
	}


	public boolean isValid() {
		boolean res = true;
		res = res && this.endState.isValid();
		res = res && this.startState.isValid();
		
		for(State s : this.states.values()) {
			res = res && s.isValid();
		}
		
		return res;
	}
	
}
