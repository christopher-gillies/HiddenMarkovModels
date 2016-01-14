package org.kidneyomics.hmm;

class State {
	
	private final Emissions emissions;
	private final Transistions transitions;
	
	State() {
		this.emissions = new Emissions();
		this.transitions = new Transistions();
	}
	
}
