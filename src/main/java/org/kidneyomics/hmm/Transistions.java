package org.kidneyomics.hmm;


public class Transistions extends AbstractProbabilityMap<State> {
	
	Transistions() {
		super();
	}
	
	Transistions(RandomNumberService service) {
		super(service);
	}
	
	Transistions(RandomNumberService service, boolean immutable) {
		super(service,immutable);
	}
	
}
