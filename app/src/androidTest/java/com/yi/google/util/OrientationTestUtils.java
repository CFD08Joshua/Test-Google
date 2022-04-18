package com.yi.google.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertTrue;

public class OrientationTestUtils {

    /**
     * Observer used in stub activities to wait for some event.
     */
    public static class Observer {
        private static final int TIMEOUT_SEC = 3;
        private final AtomicReference<CountDownLatch> mLatch = new AtomicReference();

        /**
         * Starts observing event.
         * The returned CountDownLatch will get activated when onObserved is invoked after this
         * call. The method cannot be called multiple times unless reset() is invoked.
         *
         * @return CountDownLatch will get activated when onObserved is invoked after this call.
         */
        public void startObserving() {
            final CountDownLatch latch = new CountDownLatch(1);
            assertTrue(mLatch.compareAndSet(null, latch));
        }

        /**
         * Waits until onObserved is invoked.
         */
        public void await() throws InterruptedException {
            try {
                assertTrue(mLatch.get().await(TIMEOUT_SEC, TimeUnit.SECONDS));
            } finally {
                mLatch.set(null);
            }
        }

        /**
         * Notifies an event is observed.
         * If this method is invoked after startObserving, the returned CountDownLatch will get
         * activated. Otherwise it does nothing.
         */
        public void onObserved() {
            final CountDownLatch latch = mLatch.get();
            if (latch != null) {
                latch.countDown();
            }
        }
    }
}

