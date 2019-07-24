package com.practice.threads.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CustomTreadPool {

    private int size;
    private BlockingQueue<Runnable> queue;
    private volatile boolean shutdown;

    public CustomTreadPool(int siz) {
        this.size = siz;
        shutdown = false;
        queue = new LinkedBlockingQueue<>();
        for (int i = 0; i <= size; i++) {
            MyThread thread = new MyThread();
            thread.start();
        }
    }

    public void submitTask(Runnable task) {
        queue.add(task);
    }
     public void shutDown() {
        shutdown = true;
     }

    class MyThread extends Thread {
        @Override
        public void run() {
            try {
                while(!shutdown) {
                    Runnable task = queue.poll(100, TimeUnit.MILLISECONDS);
                    if(task != null) task.run();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
