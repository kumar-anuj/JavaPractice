package com.practice.datastructures.testpractice;

import com.practice.datastructures.MyStack;

import java.util.ArrayList;
import java.util.List;

public class StackUsage {

	

	public static void main(String[] args) {
		int[] intArray = {3,7,1,5,8,2,3,9,6};
		List<Integer> secList = new ArrayList<>();
		int k = 3;
		MyStack<Integer> stack = new MyStack<Integer>(k);
		boolean hasNext = true;
		int i = 0;
		
		while(hasNext) {
			if(!stack.isStackFull() && i < 9) {
				stack.push(intArray[i]);
				i++;
			}
			else {
				while(true) {
					if(!stack.isStackEmpty()) {
						Integer ii = stack.pop();
						//System.out.print(ii);
						secList.add(ii);
					}
					else
						break;
				}
			}
				
			if(secList.size() == 9)
				hasNext = false;
			
		}
		System.out.println(secList);
	}

}
