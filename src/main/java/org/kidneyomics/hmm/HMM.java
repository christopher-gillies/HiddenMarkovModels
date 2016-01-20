package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.kidneyomics.hmm.State.VISIT_LEVEL;

public class HMM {
	
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
	public NextableOrderedSet<StateSymbolPair> generateSequence(int n) {

		
		NextableOrderedSet<StateSymbolPair> orderedSet = new NextableOrderedSet<StateSymbolPair>();
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
	public double calculateJointProbabilityOfSequencesAndStates(NextableOrderedSet<StateSymbolPair> sequence, boolean log) {
		
		double res = 0.0;
		if(sequence.size() == 0) {
			return 0.0;
		}
		
		//compute the prob(x,pi) = a_0k * [e_k(b) * akl * e_l(b) ...]
		//log prob(x,pi) = log(a_0k) + log( e_k(b) )  +log( akl + log( e_l(b)) ...]
		
		//compute transition probability into first state
		State firstSate = sequence.getFirst().getState();
		double startProb = Math.log(this.startState.getTransitions().getProbability(firstSate));
		res = startProb;

		//compute the emission probability for the state
		//compute the transition probability to next state if not null
		for(StateSymbolPair pair : sequence) {
			Symbol symbol = pair.getEmittedSymbol();
			State state = pair.getState();
			
			//if symbol is null then this is a null state 
			// that is there are no emissions so do not calculate the prob of emitting a symbol
			if(symbol != null) {
				double emitProb = Math.log(state.getEmissions().getProbability(symbol));
				res = res + emitProb;
			}
			
			if(pair.getNext() != null) {
				State nextState = pair.getNext().getState();
				double transitionProb = Math.log(state.getTransitions().getProbability(nextState));
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
		
		state.setVisitLevel(VISIT_LEVEL.VISITED);
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
			if(next.getVisitLevel() != VISIT_LEVEL.CLOSED) {
				for(State s : next.getTransitions().getKeys()) {
					if(s.getVisitLevel() == VISIT_LEVEL.NOT_VISITED) {
						s.setVisitLevel(VISIT_LEVEL.VISITED);
						queue.add(s);
					}
				}
			}
			
			//close this state b/c all reachable nodes have been added to the queue
			next.setVisitLevel(VISIT_LEVEL.CLOSED);
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
	
}
