package com.practice.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubmitRunnableTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<?> f = es.submit(() -> System.out.println("Runnable Test"));
		Thread.sleep(500);
		System.out.println("Output==>"+f.get());
		es.shutdown();
	}

}
