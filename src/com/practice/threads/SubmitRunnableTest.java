package com.practice.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubmitRunnableTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<?> f1 = es.submit( () -> System.out.println("Runnable Test"));
		Future<?> f2 =
				es.submit (
						() -> {
							System.out.println("Callable Test");
							return "Success";
						}
				);
		Thread.sleep(500);
		System.out.println("Runnable Output==>"+f1.get());
		System.out.println("Callable Output==>"+f2.get());
		es.shutdown();
	}

}
