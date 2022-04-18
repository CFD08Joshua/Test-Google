package com.yi.google.util;

import junit.framework.Assert;

import java.util.concurrent.Callable;

public abstract class PollingCheck {
    private static final long TIME_SLICE = 50;
    private long mTimeout = 3000;

    public static interface PollingCheckCondition {
        boolean canProceed();
    }

    public PollingCheck() {
    }

    public PollingCheck(long timeout) {
        mTimeout = timeout;
    }

    protected abstract boolean check();

    public void run() {
        if (check()) {
            return;
        }

        long timeout = mTimeout;
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
                Assert.fail("unexpected InterruptedException");
            }

            if (check()) {
                return;
            }

            timeout -= TIME_SLICE;
        }

        Assert.fail("unexpected timeout");
    }

    public static void check(CharSequence message, long timeout, Callable<Boolean> condition)
            throws Exception {
        while (timeout > 0) {
            if (condition.call()) {
                return;
            }

            Thread.sleep(TIME_SLICE);
            timeout -= TIME_SLICE;
        }

        Assert.fail(message.toString());
    }

    public static void waitFor(final PollingCheckCondition condition) {
        new PollingCheck() {
            @Override
            protected boolean check() {
                return condition.canProceed();
            }
        }.run();
    }

    public static void waitFor(long timeout, final PollingCheckCondition condition) {
        new PollingCheck(timeout) {
            @Override
            protected boolean check() {
                return condition.canProceed();
            }
        }.run();
    }
}
