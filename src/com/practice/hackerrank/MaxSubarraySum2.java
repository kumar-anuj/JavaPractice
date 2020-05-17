package com.practice.hackerrank;

public class MaxSubarraySum2 {

	public static void main(String[] args) {
		/* https://www.hackerrank.com/challenges/maximum-subarray-sum/problem  */
		long[] arr = {3, 3, 9, 9, 5};
		long mod = 7L;
		maximumSum(arr, mod);
	}

	static long maximumSum(long[] a, long m) {
		long result = 0L;

		for(int i = 0; i < a.length; i++) {
			int window = 1;
			long sum = 0L;
			
			System.out.println("===================");
			while(window <= a.length) {
				if(i + window > a.length)
					break;
				
				sum = sum + a[i + window -1];
				System.out.println(a[i + window -1] + ",");
				long intmdRes = sum%m;
				
				System.out.println("intmdRes==>"+intmdRes);
				if(intmdRes > result)
					result = intmdRes;
					
				window ++;
			}
		}

		System.out.println("Result===>" + result);
		return result;
	}

}
