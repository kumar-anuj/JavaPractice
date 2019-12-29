package com.practice.java8.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionPractice2 {
    public static List<String> dupList = Arrays.asList("a", "a", "b", "a", "b", "c", "c", "d", "e", "d", "e", "e");
    public static List<String> orgList = Arrays.asList("a", "b", "c", "d");

    public static void main(String[] args) {
        //Eliminate consecutive duplicates of list elements
        eliminateConsecutiveDuplicates();

        //Duplicate the elements of a list
        createConsecutiveDuplicates();
    }

    private static void createConsecutiveDuplicates() {
        List<String> newList = new ArrayList<>();
        int k = 3;
        orgList.forEach(str -> {
            for(int i=0; i<k; i++) {
                newList.add(str);
            }
        });
        System.out.println(newList);
    }

    private static void eliminateConsecutiveDuplicates() {
        List<String> newList = new ArrayList<>();

        newList.add(dupList.get(0));
        for(int i = 0; i < dupList.size()-1; ) {
            if(dupList.get(i) != dupList.get(i+1)) {
                newList.add(dupList.get(i + 1));
            }
            i++;
        }
        System.out.println(newList);
    }
}
