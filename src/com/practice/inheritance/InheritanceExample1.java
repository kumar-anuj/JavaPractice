package com.practice.inheritance;

public class InheritanceExample1 {
    public static void main(String[] args) {
        // https://stackoverflow.com/questions/19561332/in-what-order-do-static-blocks-and-initialization-blocks-execute-when-using-inhe
        A a = new B();
        a.print();
    }
}

 class A {

    static {
        System.out.println("A static");
    }
    {
        System.out.println("A init");
    }
    A() {
        super();
        System.out.println("A constructor");
    }

    A(int i) {
        super();
        System.out.println("A constructor 1");
    }

    public void display() {
        System.out.println("display");
    }

    public void  view() {
        System.out.println("view");
    }

    public  void print() {
        System.out.println("print A");
    }
}

class B extends  A{

    static {
        System.out.println("B static");
    }
    {
        System.out.println("B init");
    }
    B() {
        super();
        System.out.println("B constructor");
    }

    @Override
    public void print() {
        System.out.println("print B");
    }

    public void hello() {
        System.out.println("hello");
    }

}
