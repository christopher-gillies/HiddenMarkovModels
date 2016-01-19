package org.kidneyomics.hmm;


class Emissions extends AbstractProbabilityMap<Symbol> {

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
