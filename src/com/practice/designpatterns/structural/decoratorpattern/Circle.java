package com.practice.designpatterns.structural.decoratorpattern;


public class Circle implements MyShape{

	@Override
	public void draw() {
		System.out.println("Shape : Circle");
	}

}
