package org.kidneyomics.hmm;

class State {
	
	private final Emissions emissions;
	private final Transistions transitions;
	private final String name;
	
	State(String name) {
		this.name = name;
		this.emissions = new Emissions();
		this.transitions = new Transistions();
	}
	
	String getName() {
		return this.name;
	}
	
}
