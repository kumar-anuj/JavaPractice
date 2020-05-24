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
			
			while(i + window <= a.length) {
				sum += a[i + window -1];
				if(sum%m > result)
					result = sum%m;
				
				if(result == m-1)
					break;
					
				window ++;
			}
			if(result == m-1)
				break;
		}

		System.out.println("Result===>" + result);
		return result;
	}

}
