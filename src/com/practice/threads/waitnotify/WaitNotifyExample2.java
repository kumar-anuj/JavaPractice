package com.practice.threads.waitnotify;

public class WaitNotifyExample2 {


    public static void main(String[] args) throws InterruptedException {
        PrintNumbers pn = new PrintNumbers();
        Thread t1 = new Thread(new NewThread(pn), "ODD");
        Thread t2 = new Thread(new NewThread(pn), "EVEN");

        t1.start(); t2.start();

    }
}

class PrintNumbers {
    private boolean evenFlag = false;

    public void printOdd(int num) throws InterruptedException {
        synchronized (this) {
            while(evenFlag) {
                this.wait();
            }
            System.out.println(Thread.currentThread().getName() + "==>" + num);
            evenFlag = true;
            notify();
        }
    }

    public void printEven(int num) throws InterruptedException {
        synchronized (this) {
            while(!evenFlag) {
                this.wait();
            }
            System.out.println(Thread.currentThread().getName() + "==>" + num);
            evenFlag = false;
            notify();
        }
        }
}

class NewThread implements Runnable{
    PrintNumbers pn;

    public NewThread(PrintNumbers pn) {
        this.pn = pn;
    }

    @Override
    public void run() {
        if(Thread.currentThread().getName().equals("ODD")) {
            try {
                for(int i =1; i<11 ; i = i +2)
                    pn.printOdd(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(Thread.currentThread().getName().equals("EVEN")) {
            try {
                for(int i =2; i<11 ; i = i +2)
                 pn.printEven(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
