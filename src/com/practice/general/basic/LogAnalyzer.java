package com.practice.general.basic;

import java.util.HashMap;
import java.util.Map;

public class LogAnalyzer {

    private static final String[][] logs = {
            {"bob", "IN", "100"},
            {"bob", "OUT", "150"},
            {"alice", "IN", "200"},
            {"alice", "OUT", "250"},
            {"bob", "IN", "300"},
            {"bob", "OUT", "400"}
    };

    public static void main(String[] args) {
        Map<String, Integer> initialMap = new HashMap<>();
        Map<String, Integer> finalMap = new HashMap<>();

        for(String[] rec : logs) {
            if("IN".equalsIgnoreCase(rec[1])) {
                initialMap.put(rec[0], Integer.valueOf(rec[2]));
            }
            else if ("OUT".equalsIgnoreCase(rec[1])) {
                Integer inTime = initialMap.get(rec[0]);
                Integer outTime = Integer.parseInt(rec[2]);
                Integer totalTime = finalMap.get(rec[0]);
                if (totalTime != null)
                    finalMap.put(rec[0], totalTime + outTime - inTime);
                else
                    finalMap.put(rec[0], outTime - inTime);
            }
        }
        System.out.println(finalMap);
    }
}

