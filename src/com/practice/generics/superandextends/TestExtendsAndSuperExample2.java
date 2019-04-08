package com.practice.generics.superandextends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestExtendsAndSuperExample2 {
    public static void main(String[] args) {
        List<? extends SuperClass> lst1 = new ArrayList<>();
        List<? extends SuperClass> lst2 = Arrays.asList(new SubClassA[]{new SubClassA(1), new SubClassA(2)});
        //lst1.add(new SubClassA(11));
        //lst1.add(new SuperClass(11));

        //lst2.add(new SubClassA(3));

        List<? super SuperClass> lst3 = Arrays.asList(new SuperClass[]{new SuperClass(1), new SuperClass(2)});
        Object a = lst3.get(0);
        System.out.println(a.toString());
        List<? super SuperClass> lst4 = Arrays.asList(new SuperSuperClass[]{new SuperSuperClass(1), new SuperSuperClass(2)});
        //SuperSuperClass a = lst4.get(0);
        //System.out.println(lst4);
    }
}
