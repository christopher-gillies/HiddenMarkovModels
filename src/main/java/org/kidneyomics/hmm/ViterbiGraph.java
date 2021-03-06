package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ViterbiGraph {
	//TODO: create print function
	
	private final HMM hmm;
	private final TraversableOrderedSet<TraversableSymbol> emittedSymbols;
	private final TraversableOrderedSet<ViterbiColumn> columns;
	private ViterbiNode startNode = null;
	private ViterbiNode endNode = null;
	
	
	
	private ViterbiGraph(HMM hmm, TraversableOrderedSet<TraversableSymbol> emittedSymbols) {
		this.hmm = hmm;
		//add 2 for start column and end column
		this.columns = new TraversableOrderedSet<ViterbiColumn>();
		this.emittedSymbols = emittedSymbols;
		buildViterbiGraph();
	}
	
	public boolean forwardCalculated() {
		return this.endNode.isForwardFinished();
	}
	
	public boolean backwardCalculated() {
		return this.startNode.isBackwardFinished();
	}
	
	public ViterbiNode getStartNode() {
		return startNode;
	}



	public void setStartNode(ViterbiNode startNode) {
		this.startNode = startNode;
	}



	public ViterbiNode getEndNode() {
		return endNode;
	}



	public void setEndNode(ViterbiNode endNode) {
		this.endNode = endNode;
	}



	public HMM getHmm() {
		return hmm;
	}

	public TraversableOrderedSet<TraversableSymbol> getEmittedSymbols() {
		return emittedSymbols;
	}

	public TraversableOrderedSet<ViterbiColumn> getColumns() {
		return columns;
	}

	static ViterbiGraph createViterbiGraphFromHmmAndEmittedSymbols(HMM hmm, TraversableOrderedSet<TraversableSymbol> emittedSymbols) {
		return new ViterbiGraph(hmm, emittedSymbols);
	}
	
	//this column object will have a hash table to look up the ViterbiNodes already in this column by the states
	//this way we will not have multiple nodes that are supposed to be in the same column
	private void buildViterbiGraph() {
		State startState = this.hmm.getStartState();
		State endState = this.hmm.getEndState();
		
		//create first column, start at -1 so that the column numbers mat the position in the emittedSymbols set
		ViterbiColumn first = ViterbiColumn.createFirstColumn();
		ViterbiNode startNode = ViterbiNode.createViterbiNodeFromState(startState);
		first.addNode(startNode);
		this.columns.add(first);
		
		//set start node
		this.startNode = startNode;
		
		//create interior columns
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
		
		//set end node
		this.endNode = endNode;
		
		//set all other transitions
		setTransitions(startNode);
		
		//set transitions into last column
		//getting previous column would be really useful here
		ViterbiColumn previous = endNode.getColumn().getPrevious();
		
		//two cases, (1) a connected end state, or a disconnected end state
		//for connected end state only make edges from states that have connection to the end state
		//for unconnected any state could possibly transition to the end state
		
		if(endState.isConnectedEndState()) {
			for(ViterbiNode node : previous.getNodes()) {
				
				//if there is a non-zero transition probability to end state then add a connection
				if(node.getState().getTransitions().getProbability(endState) > 0.0) {
					//add forward edge from previous node to end node
					node.getNextNodes().add(endNode);
					//add backward edge from this node to previous node
					endNode.getPreviousNodes().add(node);
				}
			}
			
		} else {	
			for(ViterbiNode node : previous.getNodes()) {
				//add forward edge from previous node to end node
				node.getNextNodes().add(endNode);
				//add backward edge from this node to previous node
				endNode.getPreviousNodes().add(node);
			}
		}
		
	}
	
	//should be used to build from the left to the right
	private void setTransitions(ViterbiNode startNode) {
		//run bfs algorithm to create all nodes
		Queue<ViterbiNode> queue = new LinkedList<ViterbiNode>();
		queue.add(startNode);
		
		while(queue.peek() != null) {
			ViterbiNode current = queue.poll();
			current.setVisitLevel(VisitLevel.CLOSED);
			
			Collection<State> nextStates = current.getState().getTransitions().getKeys();
			for(State nextState : nextStates) {
				
				
				ViterbiNode node;
				ViterbiColumn column = null;
				//if is silent state then stay in the same column
				//otherwise increase column
				if(nextState.isSilentState()) {
					column = current.getColumn();
					node = column.getNode(nextState);
				} else {
					column = current.getColumn().getNext();
					node = column.getNode(nextState);
				}
				
				if(column.isLastColumn()) {
					//if the next column is the last column, then skip creating this
					//new node and go to next state transition
					continue;
				}
				
				if(node == null) {
					// when node is created add it to the queue
					node = ViterbiNode.createViterbiNodeFromState(nextState);
					column.addNode(node);
					queue.add(node);
				}
				
				//set backward edge to this node
				node.getPreviousNodes().add(current);
				//set forward edge from this node to next node;
				current.getNextNodes().add(node);
			}
		}
		

	}
	
	
	
}
