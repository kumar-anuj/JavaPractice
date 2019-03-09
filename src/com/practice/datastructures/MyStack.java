package com.practice.DataStructures;

import java.util.ArrayList;
import java.util.List;

public class MyStack<E> {
	
	private final List<E> lst ;
	private final int stackCapacity;
	
	public MyStack() {
		lst = new ArrayList<>(10);
		stackCapacity = 10;
	}

	public MyStack(int size) {
		lst = new ArrayList<E>(size);
		this.stackCapacity = size;
	}
	
	public boolean push(E item) {
		boolean isAdded = false;
		if(item != null && lst.size() < stackCapacity)
			isAdded = lst.add(item);
		return isAdded; 
	}
	
	public E pop() {
		if(!lst.isEmpty())
			return lst.remove(lst.size()-1);
		return null;
	}
	
	public boolean isStackEmpty() {
		return lst.size() == 0;
	}
	
	public boolean isStackFull() {
		return lst.size() == stackCapacity;
	}
	
	public E peek() {
		if(!lst.isEmpty())
			return lst.get(lst.size()-1);
		return null;
	}
	
	public int stackSize() {
		return lst.size();
	}

}
