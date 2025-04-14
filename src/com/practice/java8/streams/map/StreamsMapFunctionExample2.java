package com.practice.java8.streams.map;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsMapFunctionExample2 {

    public static void main(String[] args) {

        String para = "A paragraph is a group of sentences that develop a single idea or topic, forming a unit of thought within a larger piece of writing like an essay or article. Here's a more detailed explanation: Definition: A paragraph is a collection of sentences that are organized and coherent, all related to a single topic. Purpose: Paragraphs help readers understand the organization of a text and grasp its main points by showing where subdivisions of an essay begin and end. Structure: A well-structured paragraph typically includes a topic sentence (introducing the main idea), supporting sentences (providing details and examples), and a concluding sentence (summarizing or transitioning to the next paragraph). Length: While the length can vary, a paragraph is generally longer than a single sentence and shorter than a section or chapter. Examples: Paragraphs can describe a place, character, or process; narrate a series of events; compare or contrast two or more things; classify items into categories; or describe causes and effects. Importance: Paragraphs are the building blocks of papers and other written works, allowing for clear and organized communication of ideas.";

        List<String> strLst = Arrays.asList(para.split(" "));

        Map<String, Long> strMap = strLst.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("Map : " + strMap);

        strMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder())).limit(3).forEach(System.out::println);
        strMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).forEach(System.out::println);



    }
}
