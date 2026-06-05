package com.practice.general.basic;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

public class CollectTest {

    public static void main(String[] args) {
        // An empty set as the data source
        Set<Integer> emptySet = Collections.emptySet();

        // Stream from the empty set and collect to a new set
        Set<Integer> collectedSet = emptySet.stream()
                .map(i -> i * 2) // No elements to map
                .collect(Collectors.toSet());

        System.out.println("Collected Set (empty stream): " + collectedSet);
        System.out.println("Is collectedSet empty? " + collectedSet.isEmpty());
    }
}
