package com.yi.google;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MockReceiverDisableable extends BroadcastReceiver {
    public static final int RESULT_CODE = 99;

    @Override
    public void onReceive(Context context, Intent intent) {
        setResultCode(RESULT_CODE);
    }
}
