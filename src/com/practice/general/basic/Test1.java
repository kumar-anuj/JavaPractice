package com.practice.general.basic;

import java.awt.*;

public class Test1 {
    public static void main(String[] args) throws Exception {
        while (true) {
            Point p = MouseInfo.getPointerInfo().getLocation();
            System.out.println("Point:" + p);
            Thread.sleep(5000);
        }

    }

}