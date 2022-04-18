package com.yi.google;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MockService extends Service {
    public static boolean result = false;
    private final IBinder mBinder = new MockBinder();

    public class MockBinder extends Binder {
        MockService getService() {
            return MockService.this;
        }
    }

    /**
     * set the result as true when service bind
     */
    @Override
    public IBinder onBind(Intent intent) {
        result = true;
        return mBinder;
    }

    /**
     * set the result as true when service start
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        result = true;
    }
}
