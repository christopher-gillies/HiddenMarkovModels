package org.kidneyomics.hmm;

public class State {
	
	private static RandomNumberService randomService = new DefaultRandomNumberSerivce();
	
	private final Emissions emissions;
	private final Transistions transitions;
	private final String name;
	private final STATE_TYPE stateType;
	
	public enum STATE_TYPE {
		START,
		INTERIOR,
		END
	}
	
	public enum VISIT_LEVEL {
		NOT_VISITED,
		VISITED,
		CLOSED
	}
	
	private VISIT_LEVEL visitLevel = VISIT_LEVEL.NOT_VISITED;
	
	private State(String name, STATE_TYPE stateType) {
		this.stateType = stateType;
		this.name = name;
		if(this.isStartState()) {
			//the start state should have no emission so mark it as immutable
			this.emissions = new Emissions(randomService,true);
			this.transitions = new Transistions(randomService);
		} else if(this.isEndState()) {
			//the end state can have no transitions out of it
			//so mark it as immutable;
			this.emissions = new Emissions(randomService);
			this.transitions = new Transistions(randomService,true);
		} else {
			this.emissions = new Emissions(randomService);
			this.transitions = new Transistions(randomService);
		}
	}
	
	public static void setRandomService(RandomNumberService randomService) {
		State.randomService = randomService;
	}
	
	public static State createStartState() {
		return new State("START_STATE", STATE_TYPE.START);
	}
	
	public static State createEndState() {
		return new State("END_STATE", STATE_TYPE.END);
	}
	
	public static State createNamedStartState(String name) {
		return new State(name, STATE_TYPE.START);
	}
	
	public static State createNamedEndState(String name) {
		return new State(name, STATE_TYPE.END);
	}
	
	public static State createState(String name) {
		return new State(name, STATE_TYPE.INTERIOR);
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isStartState() {
		return this.stateType == STATE_TYPE.START;
	}
	
	public boolean isEndState() {
		return this.stateType == STATE_TYPE.END;
	}
	
	public boolean isInteriorState() {
		return this.stateType == STATE_TYPE.INTERIOR;
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
	
	public boolean isSilentState() {
		return this.emissions.isSilent();
	}

	@Override
	public String toString() {
		return this.name;
	}
	
}
