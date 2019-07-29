package com.practice.datastructures.sorting;

public class BubbleSort {
    public static void main(String[] args) {
        int[] intArray = { 20, 35, -15, 7, 55, 1, -22};
        System.out.println("====Before Sorting====");
        for(int i =0; i < intArray.length; i++)
        {
            System.out.println(intArray[i]);
        }
        /* Bubble the largest value to the end and Array will be sorted from right to left */
        for(int lastUnsortedIndex = intArray.length -1; lastUnsortedIndex > 0; lastUnsortedIndex--) {
            for(int i = 0; i<lastUnsortedIndex; i++) {
                if(intArray[i] > intArray[i+1]) {
                    swap(intArray, i, i+1);
                }
            }
        }

        System.out.println("====After Sorting====");
        for(int i =0; i < intArray.length; i++)
        {
            System.out.println(intArray[i]);
        }
    }

    public static void swap(int[]  intArray, int i, int j) {
        if(i == j) {
            return;
        }
        int temp = intArray[i];
        intArray[i] = intArray[j];
        intArray[j] = temp;
    }
}
