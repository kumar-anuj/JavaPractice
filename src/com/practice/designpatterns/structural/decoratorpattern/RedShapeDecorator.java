package com.practice.DesignPatterns.DecoratorPattern;

public class RedShapeDecorator extends MyShapeDecorator {

	public RedShapeDecorator(MyShape decoratedShape) {
		super(decoratedShape);
	}
	
	@Override
	public void draw() {
		decoratedShape.draw();
		setRedBorder(decoratedShape);
	}

	private void setRedBorder(MyShape decoratedShape) {
		System.out.println("Border Color : Red");
	}

}
