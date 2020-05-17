package com.practice.hackerrank;

public class MaxSubarraySum {

	public static void main(String[] args) {
		/* https://www.hackerrank.com/challenges/maximum-subarray-sum/problem  */
		long[] arr = {3, 3, 9, 9, 5};
		long mod = 7L;
		maximumSum(arr, mod);
	}
	
	static long maximumSum(long[] a, long m) {
		long result = 0L;
		int window = 1;
		
		while(window <= a.length) {
			
			for(int i = 0; i < a.length; i++) {
				int j = i;
				long max = 0L;
				System.out.println("====================");
				while((j < i + window) && (j < a.length) && (i + window -1 < a.length)) {
					System.out.print(a[j] + ",");
					max = max + a[j];
					j++;
				}
				
				if((i + window -1 < a.length) ) {
					long intRes = max%m;
					System.out.println("intRes===>" + intRes);

					if(intRes > result)
						result = intRes;
				}
			}
			window ++;
		}
		System.out.println("Result===>" + result);
		return result;
    }

}
