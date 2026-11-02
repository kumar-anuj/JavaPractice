package com.practice.threads.completablefuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A runnable introduction to the main CompletableFuture patterns.
 */
public class CompletableFutureExample1 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            initiatingTasks(executor);
            chainingAndTransforming(executor);
            combiningFutures(executor);
            executingInBulk(executor);
            errorHandling(executor);
        } finally {
            executor.shutdown();
        }
    }

    private static void initiatingTasks(ExecutorService executor) {
        System.out.println("\n--- Initiating tasks ---");

        CompletableFuture<Void> runnableTask = CompletableFuture.runAsync(
                () -> System.out.println("runAsync executed on " + Thread.currentThread().getName()),
                executor);

        CompletableFuture<String> supplierTask = CompletableFuture.supplyAsync(
                () -> "result from supplyAsync",
                executor);

        CompletableFuture<String> alreadyCompleted = CompletableFuture.completedFuture("completed immediately");

        CompletableFuture.allOf(runnableTask, supplierTask).join();
        System.out.println("supplyAsync: " + supplierTask.join());
        System.out.println("completedFuture: " + alreadyCompleted.join());
    }

    private static void chainingAndTransforming(ExecutorService executor) {
        System.out.println("\n--- Chaining and transforming ---");

        CompletableFuture<String> workflow = CompletableFuture.supplyAsync(
                        () -> "  java completablefuture  ", executor)
                .thenApply(String::trim)
                .thenApply(String::toUpperCase)
                .thenCompose(value -> CompletableFuture.supplyAsync(
                        () -> value + " | asynchronously composed", executor));

        workflow.thenAccept(value -> System.out.println("transformed value: " + value)).join();
    }

    private static void combiningFutures(ExecutorService executor) {
        System.out.println("\n--- Combining futures ---");

        CompletableFuture<String> user = CompletableFuture.supplyAsync(() -> "Anuj", executor);
        CompletableFuture<String> role = CompletableFuture.supplyAsync(() -> "developer", executor);

        CompletableFuture<String> combined = user.thenCombine(
                role,
                (name, job) -> name + " is a " + job);

        CompletableFuture<String> firstCompleted = user.applyToEither(
                role,
                value -> "first completed value: " + value);

        System.out.println("thenCombine: " + combined.join());
        System.out.println("applyToEither: " + firstCompleted.join());
    }

    private static void executingInBulk(ExecutorService executor) {
        System.out.println("\n--- Executing in bulk ---");

        List<String> tasks = Arrays.asList("one", "two", "three", "four");
        List<CompletableFuture<String>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(
                        () -> task.toUpperCase(), executor))
                .collect(Collectors.toList());

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture<?>[0]));

        List<String> results = allTasks.thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join();

        System.out.println("allOf results: " + results);
    }

    private static void errorHandling(ExecutorService executor) {
        System.out.println("\n--- Error handling ---");

        CompletableFuture<String> recovered = CompletableFuture.<String>supplyAsync(() -> {
            throw new IllegalStateException("simulated failure");
        }, executor).exceptionally(error -> {
            System.out.println("exceptionally saw: " + error.getCause().getMessage());
            return "fallback value";
        });

        CompletableFuture<String> inspected = CompletableFuture.supplyAsync(
                        () -> "successful value", executor)
                .handle((value, error) -> error == null ? value : "handled failure")
                .whenComplete((value, error) ->
                        System.out.println("whenComplete value: " + value));

        System.out.println("recovered result: " + recovered.join());
        System.out.println("handle result: " + inspected.join());
    }
}


