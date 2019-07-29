package com.practice.java8.streams.reduction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamReductionExample2 {
    public static void main(String[] args) {
        List<String> lst = createStringList();

        Map<String, Long> strMap = lst.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println(strMap);
        Collection<Long> values = strMap.values();
        // Collection of long to Collection of Integer
        //Collection<Integer> intValues = values.stream().mapToInt(t -> t.intValue()).boxed().collect(Collectors.toList());
        List<Integer> intValues = values.stream().mapToInt(t -> t.intValue()).boxed().collect(Collectors.toList());

        // Reverse sort
        //intValues = intValues.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        intValues.sort(Comparator.reverseOrder());

        Set<String> mapKeys = strMap.keySet();
        Map<String, Long> newMap = new LinkedHashMap<>();

        //for each value find the matching key and add to new map
        intValues.forEach( v -> mapKeys.forEach( k ->
        {
            if(strMap.get(k).intValue() == v)
                newMap.put(k,v.longValue());
        }));

        System.out.println(newMap);

    }

    public static List<String> createStringList() {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");lst.add("TWO");lst.add("ABC");lst.add("ONE");
        lst.add("TWO");lst.add("ONE");lst.add("ONE");
        lst.add("FOUR");lst.add("FOUR");lst.add("FOUR");

        return lst;
    }
}
