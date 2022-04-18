package com.yi.google.actionbar;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.yi.google.R;

import java.util.concurrent.CountDownLatch;

public class ActionBarActivity extends Activity {
    // Make sure that ActionBarActivity has focus before running Action Bar Tests.
    public CountDownLatch windowFocusSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
        windowFocusSignal = new CountDownLatch(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flat_menu, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            windowFocusSignal.countDown();
        }
    }
}
