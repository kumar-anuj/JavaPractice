package com.practice.general.basic;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class VirtualThreadExecutorExample {
    public static void main(String[] args) {
        // 1. Create an executor that spins up a new virtual thread per task
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Submit 10 concurrent tasks
            IntStream.range(0, 10).forEach(i -> {
                executor.submit(() -> {
                    System.out.println("Task " + i + " running on: " + Thread.currentThread());

                    try {
                        // Simulate blocking I/O (e.g., database call or API request)
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Task " + i + " completed!");
                });
            });

        } // The executor automatically closes here and waits for all tasks to finish

        System.out.println("All tasks finished successfully.");
    }
}

