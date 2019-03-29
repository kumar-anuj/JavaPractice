package com.practice.threads.semaphore;

import java.util.concurrent.Semaphore;

public class SemaphoreExample1 {
    public static void main(String[] args) {
        //Print Numbers in sequence using 3 threads
        Semaphore smp1 = new Semaphore(1);
        Semaphore smp2 = new Semaphore(0);
        Semaphore smp3 = new Semaphore(0);

        Thread t1 = new Thread(new NewThread(smp1, smp2, null), "Thread 1");
        Thread t2 = new Thread(new NewThread(null, smp2, smp3), "Thread 2");
        Thread t3 = new Thread(new NewThread(smp1, null, smp3), "Thread 3");

        t1.start(); t2.start(); t3.start();

    }
}

class NewThread implements Runnable {
    Semaphore smp1;
    Semaphore smp2;
    Semaphore smp3;

    public NewThread(Semaphore smp1, Semaphore smp2, Semaphore smp3) {
        this.smp1 = smp1;
        this.smp2 = smp2;
        this.smp3 = smp3;
    }

    @Override
    public void run() {
        try {
            for(int i =1 ; i<13; i++) {
                if(Thread.currentThread().getName().equals("Thread 1") && (i%3 == 1)) {
                    smp1.acquire(1);
                    //System.out.println(Thread.currentThread().getName() + ": Acquired" );
                    System.out.println(Thread.currentThread().getName() + "::"+ i);
                    smp2.release(1);
                }
                if(Thread.currentThread().getName().equals("Thread 2") && (i%3 == 2)) {
                    smp2.acquire(1);
                   //System.out.println(Thread.currentThread().getName() + ": Acquired");
                    System.out.println(Thread.currentThread().getName() + "::"+ i);
                    smp3.release(1);
                }
                if(Thread.currentThread().getName().equals("Thread 3") && (i%3 == 0)) {
                    smp3.acquire(1);
                    //System.out.println(Thread.currentThread().getName() + ": Acquired");
                    System.out.println(Thread.currentThread().getName() + "::"+ i);
                    smp1.release(1);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}