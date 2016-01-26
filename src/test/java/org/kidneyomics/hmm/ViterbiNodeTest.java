package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class ViterbiNodeTest {

	@Test
	public void testComputeLogOfSum1() {
		List<Double> list = new LinkedList<Double>();
		
		list.add(0.1);
		list.add(0.2);
		list.add(0.3);
		list.add(0.4);
		list.add(0.5);
		
		double expRes = Math.log(0.1 + 0.2 + 0.3 + 0.4 + 0.5);
		double res = ViterbiNode.computeLogOfSum(list);
		assertEquals(expRes,res,0.0001);
		
	}
	
	
	@Test
	public void testComputeLogOfSum2() {
		List<Double> list = new LinkedList<Double>();
		
		double sum = 0;
		for(double i = 0.01; i < 0.9; i+=0.001) {
			list.add(i);
			sum += i;
		}
		
		double expRes = Math.log(sum);
		double res = ViterbiNode.computeLogOfSum(list);
		assertEquals(expRes,res,0.0001);
		
	}
	
	@Test
	public void testViterbi() {
		
		State s1 = State.createState("S1");
		State s2 = State.createState("S2");
		State s3 = State.createState("S3");
		
		s2.getTransitions().setProbability(s1, 0.5);
		s3.getTransitions().setProbability(s1, 0.5);
		
		Symbol s = Symbol.createSymbol("X");
		s1.getEmissions().setProbability(s, 0.5);
		
		ViterbiColumn column = ViterbiColumn.createInteriorColumn(0, s);
		ViterbiNode node1 = ViterbiNode.createViterbiNodeFromState(s1);
		ViterbiNode node2 = ViterbiNode.createViterbiNodeFromState(s2);
		node2.setViterbi(-0.6931472);
		node2.setFinishedViterbi(true);
		ViterbiNode node3 = ViterbiNode.createViterbiNodeFromState(s3);
		node3.setViterbi(-0.8);
		node3.setFinishedViterbi(true);
		
		node1.setColumn(column);
		node1.getPreviousNodes().add(node2);
		node1.getPreviousNodes().add(node3);
		
		node1.calculateViterbi();
		
		assertTrue(node1.isFinishedViterbi());
		
		assertEquals(node2,node1.getViterbiBackPointer());
		
		assertEquals(0.5 * 0.5 * 0.5, Math.exp(node1.getViterbi()),0.0001);
		
	}
	
	
	@Test
	public void testViterbiEndState() {
		
		State s1 = State.createEndState();
		State s2 = State.createState("S2");
		State s3 = State.createState("S3");
				
		ViterbiColumn column = ViterbiColumn.createLastColumn(2);
		ViterbiNode node1 = ViterbiNode.createViterbiNodeFromState(s1);
		ViterbiNode node2 = ViterbiNode.createViterbiNodeFromState(s2);
		//log 0.5
		node2.setViterbi(-0.6931472);
		node2.setFinishedViterbi(true);
		ViterbiNode node3 = ViterbiNode.createViterbiNodeFromState(s3);
		node3.setViterbi(-0.8);
		node3.setFinishedViterbi(true);
		
		node1.setColumn(column);
		node1.getPreviousNodes().add(node2);
		node1.getPreviousNodes().add(node3);
		
		node1.calculateViterbi();
		
		assertTrue(node1.isFinishedViterbi());
		
		assertEquals(node2,node1.getViterbiBackPointer());
		
		assertEquals(0.5, Math.exp(node1.getViterbi()),0.0001);
		
	}

}
