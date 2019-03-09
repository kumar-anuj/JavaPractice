package com.practice.designpatterns.structural.decoratorpattern;

public abstract class MyShapeDecorator implements MyShape {

	protected MyShape decoratedShape;
	
	/*@Override
	public abstract void draw() ;{
		decoratedShape.draw();
	}*/

	public MyShapeDecorator(MyShape myShape) {
		super();
		this.decoratedShape = myShape;
	}

}
