package com.practice.general.basic;

public class IntTest {
    public static void main(String[] args) {
        Integer i1 =10;
        Integer i2 =10;

        Integer i3 = 127;
        Integer i4 = 127;

        System.out.println(i1==i2);
        System.out.println(i1.equals(i2));

        System.out.println(i3==i4);
        System.out.println(i3.equals(i4));
    }
}
