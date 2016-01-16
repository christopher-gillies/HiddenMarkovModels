package org.kidneyomics.hmm;

public class State {
	
	private final Emissions emissions;
	private final Transistions transitions;
	private final String name;
	private final boolean startState;
	
	public enum VISIT_LEVEL {
		NOT_VISITED,
		VISITED,
		CLOSED
	}
	
	private VISIT_LEVEL visitLevel = VISIT_LEVEL.NOT_VISITED;
	
	private State(String name, boolean startState) {
		this.startState = startState;
		this.name = name;
		this.emissions = new Emissions();
		this.transitions = new Transistions();
	}
	
	public static State createStartState() {
		return new State("START_STATE", true);
	}
	
	public static State createNamedStartState(String name) {
		return new State(name, true);
	}
	
	public static State createState(String name) {
		return new State(name, false);
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isStartState() {
		return this.startState;
	}
	
	public Symbol emitSymbol() {
		return emissions.emit();
	}
	
	public State emitNextState() {
		return transitions.emit();
	}
	
	public Transistions getTransitions() {
		return this.transitions;
	}
	
	public Emissions getEmissions() {
		return this.emissions;
	}
	
	public VISIT_LEVEL getVisitLevel() {
		return visitLevel;
	}

	public void setVisitLevel(VISIT_LEVEL visitLevel) {
		this.visitLevel = visitLevel;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
}
