package com.practice.threads.lock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample1 {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(new NewThread(lock), "Thread 1");
        Thread t2 = new Thread(new NewThread(lock), "Thread 2");

        t1.start(); t2.start();
        Thread.sleep(1000);
        System.out.println("Main t1:" + t1.isAlive());
        System.out.println("Main t2:" + t2.isAlive());
        Thread.sleep(1000);
        t1.interrupt();
        Thread.sleep(1000);
        System.out.println("Main t1:" + t1.isAlive());

    }
}

class NewThread implements Runnable {
    ReentrantLock lock;

    public NewThread(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {

        boolean flag = lock.tryLock();
        if(flag) {
            System.out.println(Thread.currentThread().getName() + " Inside lock " + flag);
            System.out.println("Interrupted " + Thread.currentThread().isInterrupted());
            while (!Thread.interrupted()) {
            }
        }
        if(lock.isHeldByCurrentThread())
            lock.unlock();
        System.out.println(Thread.currentThread().getName() + " Inside Run " + flag);
    }
}
