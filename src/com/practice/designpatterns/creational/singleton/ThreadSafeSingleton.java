package com.practice.designpatterns.creational.singleton;

public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance ;

    private ThreadSafeSingleton() { }

    public static ThreadSafeSingleton getInstance() throws InterruptedException {
        if(instance == null) {
            Thread.sleep(500);
            System.out.println("1) Thread Name : " +Thread.currentThread().getName());
            synchronized (ThreadSafeSingleton.class) {
                if(instance == null) {
                    System.out.println("2) Thread Name : " +Thread.currentThread().getName());
                    Thread.sleep(500);
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }

    public void showMessage() {
        System.out.println("ThreadSafeSingleton :" + this);
    }
}
