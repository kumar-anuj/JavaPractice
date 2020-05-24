package com.practice.hackerrank;

public class StringCompression {

	public static void main(String[] args) {
		String s = "aa"; //abcd, abcaaabbb, aaabaaaaccaaaaba
		char[] ch = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		int count = 1;
		
		for(int i=0; i<ch.length; i++) {
			if(i == 0) {
				sb.append(ch[i]);
			}
			else {
				
				if(ch[i] == ch[i-1]) {
					count ++;
				}
				else {
					if(count >1) {
						sb.append(count);
						count = 1;
					}
					sb.append(ch[i]);
				}
			}
		}
		if(count >1) 
			sb.append(count);
		System.out.println("Output ::" + sb);
	}

}
