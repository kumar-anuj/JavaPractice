package com.practice.java8.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamsFilterPredicateExample {

    public static void main(String[] args) {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");lst.add("TWO");lst.add("THREE");lst.add("FOUR");lst.add("FIVE");

        /* Predicate Examples */
        System.out.println(lst.stream().filter(pr -> pr.contains("O")).collect(Collectors.toList()));
        Predicate<String> predicate1 = (pr -> pr.contains("O"));
        Predicate<String> predicate2 = (pr -> pr.contains("F"));
        // Negate the Predicate
        System.out.println(lst.stream().filter(predicate1.negate()).collect(Collectors.toList()));
        // Predicate AND
        System.out.println(lst.stream().filter(predicate1.and(predicate2)).collect(Collectors.toList()));
        // Predicate OR
        System.out.println(lst.stream().filter(predicate1.or(predicate2)).collect(Collectors.toList()));
        // isEqual example
        Predicate<String> predicate3 = Predicate.isEqual("ONE");
        System.out.println(lst.stream().filter(predicate3).collect(Collectors.toList()));


    }
}
