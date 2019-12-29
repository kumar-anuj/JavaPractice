package com.practice.java8.practice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionPractice1 {
    public static List<String> alphList = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
    public static List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public static void main(String[] args) {
        //Find the last element of a list
        findLastElementOfList();

        //Find the last but one element of a list (Second Last)
        findSecondLastElementOfList();

        //Find the Kth element of a list
        findKthElementOfList();

    }

    private static void findKthElementOfList() {
        int k = 8;
        long count = alphList.stream().count();

        k = (k <= count) ? k : 1;

        String last = alphList.stream().skip(k - 1).limit(1).collect(Collectors.joining());
        System.out.println(last);
    }

    private static void findSecondLastElementOfList() {
        long count = alphList.stream().count();
        String last = alphList.stream().skip(count - 2).limit(1).collect(Collectors.joining());
        System.out.println(last);
    }

    private static void findLastElementOfList() {
        long count = alphList.stream().count();
        String last = alphList.stream().skip(count - 1).limit(1).collect(Collectors.joining());
        System.out.println(last);
    }
}
