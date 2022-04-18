package com.yi.google;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.yi.google.activity.MockActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class BroadcastReceiverTest {
    private static final int RESULT_INITIAL_CODE = 1;
    private static final String RESULT_INITIAL_DATA = "initial data";

    private static final int RESULT_INTERNAL_FINAL_CODE = 7;
    private static final String RESULT_INTERNAL_FINAL_DATA = "internal final data";

    private static final String ACTION_BROADCAST_INTERNAL =
            "com.yi.google.BroadcastReceiverTest.BROADCAST_INTERNAL";
    private static final String ACTION_BROADCAST_MOCKTEST =
            "com.yi.google.BroadcastReceiverTest.BROADCAST_MOCKTEST";
    private static final String ACTION_BROADCAST_TESTABORT =
            "com.yi.google.BroadcastReceiverTest.BROADCAST_TESTABORT";
    private static final String ACTION_BROADCAST_DISABLED =
            "com.yi.google.BroadcastReceiverTest.BROADCAST_DISABLED";
    private static final String TEST_PACKAGE_NAME = "com.yi.google";

    private static final String SIGNATURE_PERMISSION = "com.yi.google.SIGNATURE_PERMISSION";

    private static final long SEND_BROADCAST_TIMEOUT = 15000;
    private static final long START_SERVICE_TIMEOUT = 3000;

    private static final ComponentName DISABLEABLE_RECEIVER =
            new ComponentName("com.yi.google",
                    "com.yi.google.MockReceiverDisableable");

    private MockActivity mActivity;

    @Rule
    public ActivityScenarioRule<MockActivity> mActivityRule = new ActivityScenarioRule<>(MockActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivityRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });

    }

    @Test
    public void testConstructor() {
        new MockReceiverInternal();
    }
    @Test
    public void testAccessDebugUnregister() {
        MockReceiverInternal mockReceiver = new MockReceiverInternal();
        assertFalse(mockReceiver.getDebugUnregister());

        mockReceiver.setDebugUnregister(true);
        assertTrue(mockReceiver.getDebugUnregister());

        mockReceiver.setDebugUnregister(false);
        assertFalse(mockReceiver.getDebugUnregister());
    }
    @Test
    public void testSetOrderedHint() {
        MockReceiverInternal mockReceiver = new MockReceiverInternal();

        /*
         * Let's just test to make sure the method doesn't fail for this one.
         * It's marked as "for internal use".
         */
        mockReceiver.setOrderedHint(true);
        mockReceiver.setOrderedHint(false);
    }

    private class MockReceiverInternal extends BroadcastReceiver {
        protected boolean mCalledOnReceive = false;
        private IBinder mIBinder;

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            mCalledOnReceive = true;
            Intent serviceIntent = new Intent(context, MockService.class);
            mIBinder = peekService(context, serviceIntent);
            notifyAll();
        }

        public boolean hasCalledOnReceive() {
            return mCalledOnReceive;
        }

        public void reset() {
            mCalledOnReceive = false;
        }

        public synchronized void waitForReceiver(long timeout)
                throws InterruptedException {
            if (!mCalledOnReceive) {
                wait(timeout);
            }
            assertTrue(mCalledOnReceive);
        }

        public synchronized boolean waitForReceiverNoException(long timeout)
                throws InterruptedException {
            if (!mCalledOnReceive) {
                wait(timeout);
            }
            return mCalledOnReceive;
        }

        public IBinder getIBinder() {
            return mIBinder;
        }
    }

    private class MockReceiverInternalOrder extends MockReceiverInternal {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            setResultCode(RESULT_INTERNAL_FINAL_CODE);
            setResultData(RESULT_INTERNAL_FINAL_DATA);

            super.onReceive(context, intent);
        }
    }

    private class MockReceiverInternalVerifyUncalled extends MockReceiverInternal {
        final int mExpectedInitialCode;

        public MockReceiverInternalVerifyUncalled(int initialCode) {
            mExpectedInitialCode = initialCode;
        }

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            // only update to the expected final values if we're still in the
            // initial conditions.  The intermediate receiver would have
            // updated the result code if it [inappropriately] ran.
            if (getResultCode() == mExpectedInitialCode) {
                setResultCode(RESULT_INTERNAL_FINAL_CODE);
            }

            super.onReceive(context, intent);
        }
    }
    @Test
    public void testOnReceive() throws InterruptedException {
        final MockActivity activity = mActivity;

        MockReceiverInternal internalReceiver = new MockReceiverInternal();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BROADCAST_INTERNAL);
        activity.registerReceiver(internalReceiver, filter);

        assertEquals(0, internalReceiver.getResultCode());
        assertEquals(null, internalReceiver.getResultData());
        assertEquals(null, internalReceiver.getResultExtras(false));

        activity.sendBroadcast(new Intent(ACTION_BROADCAST_INTERNAL)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND));
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        activity.unregisterReceiver(internalReceiver);
    }
    @Test
    public void testManifestReceiverPackage() throws InterruptedException {
        MockReceiverInternal internalReceiver = new MockReceiverInternal();

        Bundle map = getBundle();
        mActivity.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_MOCKTEST)
                        .setPackage(TEST_PACKAGE_NAME).addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                null, internalReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, map);
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        // These are set by MockReceiver.
        assertEquals(MockReceiver.RESULT_CODE, internalReceiver.getResultCode());
        assertEquals(MockReceiver.RESULT_DATA, internalReceiver.getResultData());

        Bundle resultExtras = internalReceiver.getResultExtras(false);
        assertEquals(MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY));
        assertEquals(MockReceiver.RESULT_EXTRAS_ADD_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_ADD_KEY));
        assertNull(resultExtras.getString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY));
    }
    @Test
    public void testManifestReceiverComponent() throws InterruptedException {
        MockReceiverInternal internalReceiver = new MockReceiverInternal();

        Bundle map = getBundle();
        mActivity.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_MOCKTEST)
                        .setClass(mActivity, MockReceiver.class)
                        .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                null, internalReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, map);
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        // These are set by MockReceiver.
        assertEquals(MockReceiver.RESULT_CODE, internalReceiver.getResultCode());
        assertEquals(MockReceiver.RESULT_DATA, internalReceiver.getResultData());

        Bundle resultExtras = internalReceiver.getResultExtras(false);
        assertEquals(MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY));
        assertEquals(MockReceiver.RESULT_EXTRAS_ADD_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_ADD_KEY));
        assertNull(resultExtras.getString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY));
    }
    @Test
    public void testManifestReceiverPermission() throws InterruptedException {
        MockReceiverInternal internalReceiver = new MockReceiverInternal();

        Bundle map = getBundle();
        mActivity.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_MOCKTEST)
                        .addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                SIGNATURE_PERMISSION, internalReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, map);
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        // These are set by MockReceiver.
        assertEquals(MockReceiver.RESULT_CODE, internalReceiver.getResultCode());
        assertEquals(MockReceiver.RESULT_DATA, internalReceiver.getResultData());

        Bundle resultExtras = internalReceiver.getResultExtras(false);
        assertEquals(MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY));
        assertEquals(MockReceiver.RESULT_EXTRAS_ADD_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_ADD_KEY));
        assertNull(resultExtras.getString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY));
    }
    @Test
    public void testNoManifestReceiver() throws InterruptedException {
        MockReceiverInternal internalReceiver = new MockReceiverInternal();

        Bundle map = getBundle();
        mActivity.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_MOCKTEST).addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                null, internalReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, map);
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        // The MockReceiver should not have run, so we should still have the initial result.
        assertEquals(RESULT_INITIAL_CODE, internalReceiver.getResultCode());
        assertEquals(RESULT_INITIAL_DATA, internalReceiver.getResultData());

        Bundle resultExtras = internalReceiver.getResultExtras(false);
        assertEquals(MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY));
        assertNull(resultExtras.getString(MockReceiver.RESULT_EXTRAS_ADD_KEY));
        assertEquals(MockReceiver.RESULT_EXTRAS_REMOVE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY));
    }
    @Test
    public void testAbortBroadcast() throws InterruptedException {
        MockReceiverInternalOrder internalOrderReceiver = new MockReceiverInternalOrder();

        assertEquals(0, internalOrderReceiver.getResultCode());
        assertNull(internalOrderReceiver.getResultData());
        assertNull(internalOrderReceiver.getResultExtras(false));

        Bundle map = getBundle();
        // The order of the receiver is:
        // MockReceiverFirst --> MockReceiverAbort --> MockReceiver --> internalOrderReceiver.
        // And MockReceiver is the receiver which will be aborted.
        mActivity.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_TESTABORT)
                        .setPackage(TEST_PACKAGE_NAME).addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                null, internalOrderReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, map);
        internalOrderReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        assertEquals(RESULT_INTERNAL_FINAL_CODE, internalOrderReceiver.getResultCode());
        assertEquals(RESULT_INTERNAL_FINAL_DATA, internalOrderReceiver.getResultData());
        Bundle resultExtras = internalOrderReceiver.getResultExtras(false);
        assertEquals(MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY));
        assertEquals(MockReceiver.RESULT_EXTRAS_REMOVE_VALUE,
                resultExtras.getString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY));
        assertEquals(MockReceiverFirst.RESULT_EXTRAS_FIRST_VALUE,
                resultExtras.getString(MockReceiverFirst.RESULT_EXTRAS_FIRST_KEY));
        assertEquals(MockReceiverAbort.RESULT_EXTRAS_ABORT_VALUE,
                resultExtras.getString(MockReceiverAbort.RESULT_EXTRAS_ABORT_KEY));
    }

    private Bundle getBundle() {
        Bundle map = new Bundle();
        map.putString(MockReceiver.RESULT_EXTRAS_INVARIABLE_KEY,
                MockReceiver.RESULT_EXTRAS_INVARIABLE_VALUE);
        map.putString(MockReceiver.RESULT_EXTRAS_REMOVE_KEY,
                MockReceiver.RESULT_EXTRAS_REMOVE_VALUE);
        return map;
    }

    @Test
    public void testDisabledBroadcastReceiver() throws Exception {
        final Context context = mActivity;
        PackageManager pm = context.getPackageManager();

        MockReceiverInternalVerifyUncalled lastReceiver =
                new MockReceiverInternalVerifyUncalled(RESULT_INITIAL_CODE);
        assertEquals(0, lastReceiver.getResultCode());

        pm.setComponentEnabledSetting(DISABLEABLE_RECEIVER,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        context.sendOrderedBroadcast(
                new Intent(ACTION_BROADCAST_DISABLED).addFlags(Intent.FLAG_RECEIVER_FOREGROUND),
                null, lastReceiver,
                null, RESULT_INITIAL_CODE, RESULT_INITIAL_DATA, new Bundle());
        lastReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);

        assertEquals(RESULT_INTERNAL_FINAL_CODE, lastReceiver.getResultCode());
    }
    @Test
    public void testPeekService() throws InterruptedException {
        final MockActivity activity = mActivity;

        MockReceiverInternal internalReceiver = new MockReceiverInternal();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BROADCAST_INTERNAL);
        activity.registerReceiver(internalReceiver, filter);

        activity.sendBroadcast(new Intent(ACTION_BROADCAST_INTERNAL)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND));
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);
        assertNull(internalReceiver.getIBinder());

        Intent intent = new Intent(activity, MockService.class);
        MyServiceConnection msc = new MyServiceConnection();
        assertTrue(activity.bindService(intent, msc, Service.BIND_AUTO_CREATE));
        assertTrue(msc.waitForService(START_SERVICE_TIMEOUT));

        internalReceiver.reset();
        activity.sendBroadcast(new Intent(ACTION_BROADCAST_INTERNAL)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND));
        internalReceiver.waitForReceiver(SEND_BROADCAST_TIMEOUT);
        assertNotNull(internalReceiver.getIBinder());
        activity.unbindService(msc);
        activity.stopService(intent);
        activity.unregisterReceiver(internalReceiver);
    }
    @Test
    public void testNewPhotoBroadcast_notReceived() throws InterruptedException {
        final MockActivity activity = mActivity;
        MockReceiverInternal internalReceiver = new MockReceiverInternal();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Camera.ACTION_NEW_PICTURE);
        activity.registerReceiver(internalReceiver, filter);
        assertFalse(internalReceiver.waitForReceiverNoException(SEND_BROADCAST_TIMEOUT));
    }
    @Test
    public void testNewVideoBroadcast_notReceived() throws InterruptedException {
        final MockActivity activity = mActivity;
        MockReceiverInternal internalReceiver = new MockReceiverInternal();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Camera.ACTION_NEW_VIDEO);
        activity.registerReceiver(internalReceiver, filter);
        assertFalse(internalReceiver.waitForReceiverNoException(SEND_BROADCAST_TIMEOUT));
    }

    static class MyServiceConnection implements ServiceConnection {
        private boolean serviceConnected;

        public synchronized void onServiceConnected(ComponentName name, IBinder service) {
            serviceConnected = true;
            notifyAll();
        }

        public synchronized void onServiceDisconnected(ComponentName name) {
        }

        public synchronized boolean waitForService(long timeout) {
            if (!serviceConnected) {
                try {
                    wait(timeout);
                } catch (InterruptedException ignored) {
                    // ignored
                }
            }
            return serviceConnected;
        }
    }
}
