package com.practice.datastructures.sorting;

public class SelectIonSort {
    public static void main(String[] args) {
        int[] intArray = { 20, 35, -15, 7, 55, 1, -22};
        System.out.println("====Before Sorting====");
        for(int i =0; i < intArray.length; i++)
        {
            System.out.println(intArray[i]);
        }
        /* In each inner iteration select the largest index and swap with last unsorted index and Array will be sorted from right to left */
        for(int lastUnsortedIndex = intArray.length -1; lastUnsortedIndex > 0; lastUnsortedIndex--) {
            int largest = 0;
            for(int i = 0; i<=lastUnsortedIndex; i++) {
                if(intArray[i] > intArray[largest]) {
                    largest = i;
                }
            }
            swap(intArray, largest, lastUnsortedIndex);
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
