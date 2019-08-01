package com.practice.java8.streams.predicate;

import java.util.*;
import java.util.function.Predicate;

public class StreamPredicateExample1 {
    public static void main(String[] args) {
        List<String> strLst = Collections.unmodifiableList(Arrays.asList("ONE", "TWO", "THREE", "FOUR"));

        Set<String> strSet = new HashSet<>();
        strSet.add("ONE");
        strSet.add("TWO");
        strSet.add("THREE");

        System.out.println(strLst);
        System.out.println(strSet);

        Predicate<String> pr = str  -> strLst.contains(str);
        boolean flag = strSet.stream().allMatch(pr);
        System.out.println(flag);

        System.out.println(strLst.containsAll(strSet));
    }
}
