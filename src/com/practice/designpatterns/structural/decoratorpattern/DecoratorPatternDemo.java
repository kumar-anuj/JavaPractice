package com.practice.designpatterns.structural.decoratorpattern;

public class DecoratorPatternDemo {
	public static void main(String[] args) {

		MyShape circle = new Circle();
		MyShape redCircle = new RedShapeDecorator(new Circle());
		MyShape redRectangle = new RedShapeDecorator(new Rectangle());
		
		System.out.println("Circle with normal border");
		circle.draw();

		System.out.println("\nCircle of red border");
		redCircle.draw();

		System.out.println("\nRectangle of red border");
		redRectangle.draw();
	}
}
