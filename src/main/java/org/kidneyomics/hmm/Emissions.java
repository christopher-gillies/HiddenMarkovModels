package org.kidneyomics.hmm;


public class Emissions extends AbstractProbabilityMap<Symbol> {

	Emissions() {
		super();
	}
	
	Emissions(RandomNumberService service) {
		super(service);
	}
	
	Emissions(RandomNumberService service, boolean immutable) {
		super(service,immutable);
	}
	
}
