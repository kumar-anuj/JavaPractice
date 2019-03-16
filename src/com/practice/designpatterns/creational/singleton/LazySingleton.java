package com.practice.designpatterns.creational.singleton;

public class LazySingleton {
    private static LazySingleton instance ;

    private LazySingleton() { }

    public static LazySingleton getInstance() throws InterruptedException {

        if(instance == null) {
            System.out.println("Thread Name : " +Thread.currentThread().getName());
            Thread.sleep(1000);
            instance = new LazySingleton();
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("LazySingleton :" + this);
    }
}
