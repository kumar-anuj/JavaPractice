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
        System.out.println("Super static");
    }
    {
        System.out.println("Super init");
    }
    A() {
        super();
        System.out.println("Super constructor");
    }

    A(int i) {
        super();
        System.out.println("Super constructor 1");
    }

    public void display() {
        System.out.println("display Super");
    }

    public void  view() {
        System.out.println("view Super");
    }

    public  void print() {
        System.out.println("print Super");
    }
}

class B extends  A{

    static {
        System.out.println("Sub static");
    }
    {
        System.out.println("Sub init");
    }
    B() {
        super();
        System.out.println("Sub constructor");
    }

    @Override
    public void print() {
        System.out.println("print Sub");
    }

    public void hello() {
        System.out.println("hello Sub");
    }

}
