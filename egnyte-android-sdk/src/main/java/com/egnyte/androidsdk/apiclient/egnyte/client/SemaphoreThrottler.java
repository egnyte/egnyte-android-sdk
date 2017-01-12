package com.egnyte.androidsdk.apiclient.egnyte.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class SemaphoreThrottler {

    private final Semaphore semaphore;
    private final Integer qps;
    private final ScheduledExecutorService scheduledExecutorService;

    public SemaphoreThrottler(Integer qps) {
        if (qps != null && qps > 0) {
            this.semaphore = new Semaphore(qps, true);
        } else {
            semaphore = null;
        }
        this.qps = qps;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void waitForExecution() {
        if (semaphore != null) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
            }
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    if (semaphore.availablePermits() < qps) {
                        semaphore.release();
                    }
                }
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    public void onTaskCancelled() {
        if (semaphore != null && semaphore.availablePermits() < qps) {
            semaphore.release();
        }
    }
}