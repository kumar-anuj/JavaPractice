package com.practice.hackerrank;

import java.util.Stack;

public class StringCompression2 {

	public static void main(String[] args) {
		String s = "aa"; //abcd, abcaaabbb, aaabaaaaccaaaaba
		char[] ch = s.toCharArray();
		Stack<Character> stack = new Stack<>();
		StringBuilder sb = new StringBuilder();
		int size = 0;
		
		for(int i=0; i<ch.length; i++) {
			if(stack.isEmpty())
				stack.push(ch[i]);
			else {
				if(ch[i] == stack.peek()) {
					stack.push(ch[i]);
				}
				else {
					size = stack.size();
					sb.append(stack.peek());
					
					if(size > 1)
						sb.append(size);
					
					stack.clear();
					stack.push(ch[i]);
				}
			}
		}
		if(!stack.isEmpty())
			sb.append(stack.peek());
		if(stack.size() > 1)
			sb.append(stack.size());
		
		System.out.println("Output ::" + sb);
	}

}
