package com.practice.DesignPatterns.StrategyPattern;

public class Context {

	Strategy strategy;

	public Context(Strategy strategy) {
		super();
		this.strategy = strategy;
	}
	
	public int executeStrategy(int i1, int i2) {
		return strategy.doOperation(i1, i2);
	}
	
	
}
