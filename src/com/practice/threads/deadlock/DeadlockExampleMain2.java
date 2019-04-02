package com.practice.threads.deadlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockExampleMain2 {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread(), "Thread 1");
        t1.start();
    }
}

class MyThread implements Runnable {
    ReentrantLock lock = new ReentrantLock();
    Condition noLock = lock.newCondition();

    @Override
    public void run() {
        if(lock.getHoldCount() == 0) {
            lock.lock();
            System.out.println("In Run");
            display();
            lock.unlock();
        }
        else {
            try {
                System.out.println("run waiting");
                noLock.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void display() {
        if(lock.getHoldCount() == 0) {
            lock.lock();
            System.out.println("In display :" + lock.getHoldCount());
            lock.unlock();
        }
        else {
            try {
                System.out.println("display waiting");
                noLock.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
