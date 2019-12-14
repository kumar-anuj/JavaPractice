package com.practice.inheritance;

public class InheritanceExample2 {
    public static void main(String[] args) {
        MySuper sup = new MySub();
        MySub sub = new MySub();

        new InheritanceExample2().print(sup, sub);
    }

    private void print(MySuper sup, MySub sub) {
        System.out.println("Subclass :" + sub.i);
        System.out.println("Superclass :" + sup.i);
        sub.display();
        sup.display();
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