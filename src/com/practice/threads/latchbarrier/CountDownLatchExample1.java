package com.practice.threads.latchbarrier;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample1 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " start");
        CountDownLatch latch = new CountDownLatch(4);
        Thread t1 = new Thread(new NewThread(latch), "Thread1");
        Thread t2 = new Thread(new NewThread(latch), "Thread2");
        Thread t3 = new Thread(new NewThread(latch), "Thread3");
        Thread t4 = new Thread(new NewThread(latch), "Thread4");

        t1.start(); t2.start(); t3.start(); t4.start();
        System.out.println("Threads Started");
        latch.await();
        System.out.println(Thread.currentThread().getName() + " finish");
    }
}

class NewThread implements Runnable {
    CountDownLatch latch;

    public NewThread(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Running");
        latch.countDown();
        System.out.println(Thread.currentThread().getName() + " Finished");
        //latch.countDown();
    }
}