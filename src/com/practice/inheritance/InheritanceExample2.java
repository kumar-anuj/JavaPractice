package com.practice.inheritance;

public class InheritanceExample2 {
    public static void main(String[] args) {
        MySuper sup = new MySub();
        MySub sub = new MySub();

        System.out.println(sub.i);
        System.out.println(sup.i);
    }
}

class MySuper {
    int i = 10;

    public void display() {
        System.out.println("Display MySuper");
    }
}

class MySub extends  MySuper {
    int i = 20;

    @Override
    public void display() {
        System.out.println("Display MySub");
    }
}