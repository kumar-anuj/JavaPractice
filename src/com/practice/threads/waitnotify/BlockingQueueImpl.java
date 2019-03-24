package com.practice.threads.waitnotify;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueueImpl<T> {
    private Queue<T> queue = new LinkedList<T>();
    private int capacity;

    public BlockingQueueImpl(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T element) throws InterruptedException {
        if(queue.size() == capacity) {
            System.out.println("Queue Full");
            wait();
        }
        Thread.sleep(1000);
        queue.add(element);
        System.out.println("Item added: "+ element);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        if(queue.isEmpty()) {
            System.out.println("Queue Empty");
            wait();
        }

        T item = queue.remove();
        System.out.println("Item removed: "+ item);
        notifyAll();
        return item;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Start");
        BlockingQueueImpl<String> queue = new BlockingQueueImpl<String>(10);
        Thread t1 = new Thread(new AddElement(queue), "AddElement");
        Thread t2 = new Thread(new RemoveElement(queue), "RemoveElement");
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Main Finish");

    }
}

class AddElement implements Runnable {
    BlockingQueueImpl<String> queue;

    public AddElement(BlockingQueueImpl<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " thread started");
        for(int i =0; i <20; i++) {
            try {
                queue.put("A" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class RemoveElement implements Runnable {
    BlockingQueueImpl<String> queue;

    public RemoveElement(BlockingQueueImpl<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " thread started");
        for(int i =0; i <20; i++) {
            try {
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}