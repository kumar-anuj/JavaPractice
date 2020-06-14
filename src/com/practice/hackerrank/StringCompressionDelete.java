package com.practice.hackerrank;

import java.util.Iterator;
import java.util.Stack;

public class StringCompressionDelete {

	public static void main(String[] args) {
		String s = "abbbabbbacc"; //abcd, abbcccbd, aabbbaccaaaacbbbaa, aabbbaccaaabbbcaa
		char[] ch = s.toCharArray();
		
		Stack<Character> stack = new Stack<>();
		int count = 0;
		int previousCount = 0;
		final int k = 3;
		
		for(int i=0; i<ch.length; i++) {
			if(stack.isEmpty()) {
				stack.push(ch[i]);
				count ++;
			}
			else {
				if(ch[i] == stack.peek()) {
					stack.push(ch[i]);
					count ++;
					
					if(count == k) {
						while(count > 0) {
							stack.pop();
							count --;
						}
						count = previousCount;
						previousCount = 0;
					}
				}
				else {
					stack.push(ch[i]);
					previousCount = count;
					count = 1;
				}
			}
		}
		Iterator<Character> itr = stack.iterator();
		
		while(itr.hasNext()) {
			System.out.print(itr.next());
		}
		
	}

}
