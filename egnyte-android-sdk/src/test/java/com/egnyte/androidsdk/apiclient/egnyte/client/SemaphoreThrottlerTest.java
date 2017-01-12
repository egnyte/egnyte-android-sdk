package com.egnyte.androidsdk.apiclient.egnyte.client;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreThrottlerTest {

    @Test
    public void test() throws InterruptedException {
        int qps = 0;
        int n = 24;
        final Semaphore testSemaphore = new Semaphore(-n + 1);
        final long[] executionTime = new long[n];
        final SemaphoreThrottler throttler = new SemaphoreThrottler(qps);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < n; ++i) {
            final int finalI = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    throttler.waitForExecution();
                    executionTime[finalI] = System.currentTimeMillis();
                    testSemaphore.release();
                }
            });
        }
        testSemaphore.acquire();

        for (int i = 0; i < n; ++i) {
            System.out.println(executionTime[i]);
        }
    }


    @Test
    public void testWithNoQps() throws InterruptedException {
        final String currentThread = Thread.currentThread().toString();
        int qps = 0;
        int n = 6;
        final Semaphore testSemaphore = new Semaphore(-n + 1);
        final long[] executionTime = new long[n];
        final String[] threads = new String[n];

        final SemaphoreThrottler throttler = new SemaphoreThrottler(qps);
        for (int i = 0; i < n; ++i) {
            final int finalI = i;
            throttler.waitForExecution();
            executionTime[finalI] = System.currentTimeMillis() / 1000;
            threads[finalI] = Thread.currentThread().toString();
            testSemaphore.release();
        }
        testSemaphore.acquire();

        for (long execution : executionTime) {
            System.out.println(execution);
        }
        for (String s : threads) {
            Assert.assertEquals(currentThread, s);
        }
    }

    @Test
    public void testWithQPS() throws InterruptedException {
        final String currentThread = Thread.currentThread().toString();
        int qps = 3;
        int n = 6;
        final Semaphore testSemaphore = new Semaphore(-n + 1);
        final long[] executionTime = new long[n];
        final String[] threads = new String[n];

        final SemaphoreThrottler throttler = new SemaphoreThrottler(qps);
        for (int i = 0; i < n; ++i) {
            final int finalI = i;
            System.out.println("waitForExecution on " + Thread.currentThread());
            throttler.waitForExecution();
            executionTime[finalI] = System.currentTimeMillis() / 1000;
            threads[finalI] = Thread.currentThread().toString();
            testSemaphore.release();
        }
        System.out.println("test semaphore acqiure");
        testSemaphore.acquire();

        for (long execution : executionTime) {
            System.out.println(execution);
        }
        for (String s : threads) {
            Assert.assertEquals(currentThread, s);
        }
    }
}