package com.practice.threads.latchbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample1 {
    public static void main(String[] args) {
        CyclicBarrier barrier1 = new CyclicBarrier(3);
        CyclicBarrier barrier2 = new CyclicBarrier(3);
        Thread t1 = new Thread(new NewThread1(barrier1, barrier2), "Thread1");
        Thread t2 = new Thread(new NewThread1(barrier1, barrier2), "Thread2");
        Thread t3 = new Thread(new NewThread1(barrier1, barrier2), "Thread3");

        barrier1.getNumberWaiting();
        t1.start(); t2.start(); t3.start();

        barrier1.reset();
        barrier2.reset();

        Thread t4 = new Thread(new NewThread1(barrier1, barrier2), "Thread4");
        Thread t5 = new Thread(new NewThread1(barrier1, barrier2), "Thread5");
        Thread t6 = new Thread(new NewThread1(barrier1, barrier2), "Thread6");
        t4.start(); t5.start(); t6.start();
    }
}

class NewThread1 implements Runnable {
    CyclicBarrier barrier1, barrier2;

    public NewThread1(CyclicBarrier barrier1, CyclicBarrier barrier2) {
        this.barrier1 = barrier1;
        this.barrier2 = barrier2;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " barrier 1::" + barrier1.getParties());
        try {
            Thread.sleep(3000);
            barrier1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " barrier 2::" + barrier2.getParties());

        try {
            Thread.sleep(3000);
            barrier2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " Finished");
    }
}
