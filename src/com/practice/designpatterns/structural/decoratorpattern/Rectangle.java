package com.practice.designpatterns.structural.decoratorpattern;


public class Rectangle implements MyShape{

	@Override
	public void draw() {
		System.out.println("Shape : Rectangle");
	}

}
