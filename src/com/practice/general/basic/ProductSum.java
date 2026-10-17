package com.practice.general.basic;

import java.util.Arrays;
import java.util.Comparator;

public class ProductSum {
    public static void main(String[] args) {
        Integer[] a = {1,3,2,4}, b = {5,1,3,2};

        System.out.println("Minimum product sum is :"+ getMinimumProductSum(a, b));

    }

    private static int getMinimumProductSum(Integer[] a, Integer[] b) {
        int length = a.length;
        int min = 0;
        Arrays.sort(a);
        Arrays.sort(b, Comparator.reverseOrder());

        for(int i=0; i<length; i++) {
            //min = min + a[i]*b[length -i -1];
            min = min + a[i]*b[i];
        }
        return min;

    }
}
