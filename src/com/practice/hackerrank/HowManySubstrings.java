package com.practice.hackerrank;

import java.util.HashSet;
import java.util.Set;

public class HowManySubstrings {

	public static void main(String[] args) {
		String s = "aabaa";
		int[][] queries = {{1,1}, {1,4}, {0,2}};
		countSubstrings(s, queries);
	}

	public static int[] countSubstrings(String s, int[][] queries) {
		int[] result = new int[queries.length];
		
		for(int i=0; i <queries.length ; i++) {
			int start = queries[i][0];
			int end = queries[i][1];
			
			String sub = s.substring(start, end +1);
			
			result[i] = countSubstrings(sub);
		}
		
		return result;
	}

	private static int countSubstrings(String sub) {
		Set<String> strSet = new HashSet<>();
		char[] ch = sub.toCharArray();
		
		
		for(int i =0; i<ch.length; i++) {
			int window = 0;
			StringBuilder sb = new StringBuilder();
			
			while(i + window < ch.length) {
				sb.append(ch[i + window]);
				strSet.add(sb.toString());
			
				window++;
			}
			
		}
		return strSet.size();
	}
}
