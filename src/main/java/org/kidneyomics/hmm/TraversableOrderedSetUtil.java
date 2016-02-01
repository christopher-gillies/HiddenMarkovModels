package org.kidneyomics.hmm;

import java.util.LinkedList;
import java.util.List;

public class TraversableOrderedSetUtil {
	/**
	 * 
	 * @param symbols -- a list of symbols to be converted to traversable list of symbols
	 * @return traversable list of symbols
	 */
	public static TraversableOrderedSet<TraversableSymbol> symbolListToTraverseable(List<Symbol> symbols) {
		
		TraversableOrderedSet<TraversableSymbol> symbolsOut = new TraversableOrderedSet<TraversableSymbol>();
		
		for(Symbol symbol : symbols) {
			symbolsOut.add(new TraversableSymbol(symbol));
		}
		
		return symbolsOut;
	}
	
	/**
	 * 
	 * @param listOfListOfSymbols -- a list of list of symbols
	 * @return a list of traversable list of symbols
	 */
	public static List<TraversableOrderedSet<TraversableSymbol>> listOflistOfSymbolsToListOfTranversable(List<List<Symbol>> listOfListOfSymbols) {
		LinkedList<TraversableOrderedSet<TraversableSymbol>> list = new LinkedList<TraversableOrderedSet<TraversableSymbol>>();
		for(List<Symbol> listOfSymbols : listOfListOfSymbols) {
			TraversableOrderedSet<TraversableSymbol> traversable = symbolListToTraverseable(listOfSymbols);
			list.add(traversable);
		}
		return list;
	}
}
