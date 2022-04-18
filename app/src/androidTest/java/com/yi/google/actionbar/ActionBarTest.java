package com.yi.google.actionbar;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ActionBarTest  {

    private ActionBarActivity mActivity;
    private ActionBar mBar;

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(ActionBarActivity.class);

    @Before
    public void setUp() throws Exception {
        rule.getScenario().onActivity(activity -> {
            mActivity = (ActionBarActivity) activity;
        });
        mBar = mActivity.getActionBar();
    }

    @Test
    public void testAddTab() {
        if (mBar == null) {
            return;
        }
        assertEquals(0, mBar.getTabCount());

        ActionBar.Tab t1 = createTab("Tab 1");
        mBar.addTab(t1);
        assertEquals(1, mBar.getTabCount());
        assertEquals(t1, mBar.getSelectedTab());
        assertEquals(t1, mBar.getTabAt(0));

        ActionBar.Tab t2 = createTab("Tab 2");
        mBar.addTab(t2);
        assertEquals(2, mBar.getTabCount());
        assertEquals(t1, mBar.getSelectedTab());
        assertEquals(t2, mBar.getTabAt(1));

        ActionBar.Tab t3 = createTab("Tab 3");
        mBar.addTab(t3, true);
        assertEquals(3, mBar.getTabCount());
        assertEquals(t3, mBar.getSelectedTab());
        assertEquals(t3, mBar.getTabAt(2));

        ActionBar.Tab t4 = createTab("Tab 2.5");
        mBar.addTab(t4, 2);
        assertEquals(4, mBar.getTabCount());
        assertEquals(t4, mBar.getTabAt(2));
        assertEquals(t3, mBar.getTabAt(3));

        ActionBar.Tab t5 = createTab("Tab 0.5");
        mBar.addTab(t5, 0, true);
        assertEquals(5, mBar.getTabCount());
        assertEquals(t5, mBar.getSelectedTab());
        assertEquals(t5, mBar.getTabAt(0));
        assertEquals(t1, mBar.getTabAt(1));
        assertEquals(t2, mBar.getTabAt(2));
        assertEquals(t4, mBar.getTabAt(3));
        assertEquals(t3, mBar.getTabAt(4));
    }

    @Test
    public void testOptionsMenuKey() throws Exception {
        boolean hasPermanentMenuKey = ViewConfiguration.get(mActivity).hasPermanentMenuKey();
        if (!mActivity.getWindow().hasFeature(Window.FEATURE_OPTIONS_PANEL)
                || hasPermanentMenuKey) {
            return;
        }
        final boolean menuIsVisible[] = {false};
        mActivity.getActionBar().addOnMenuVisibilityListener(
                isVisible -> menuIsVisible[0] = isVisible);
        // Wait here for test activity to gain focus before sending keyevent.
        // Visibility listener needs the action bar to be visible.
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

        assertTrue(menuIsVisible[0]);
        assertTrue(mActivity.windowFocusSignal.await(1000, TimeUnit.MILLISECONDS));
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

        assertTrue(mActivity.windowFocusSignal.await(1000, TimeUnit.MILLISECONDS));
        assertFalse(menuIsVisible[0]);
    }

    @Test
    public void testOpenOptionsMenu() {
        boolean hasPermanentMenuKey = ViewConfiguration.get(mActivity).hasPermanentMenuKey();
        if (!mActivity.getWindow().hasFeature(Window.FEATURE_OPTIONS_PANEL)
                || hasPermanentMenuKey) {
            return;
        }
        final boolean menuIsVisible[] = {false};
        mActivity.getActionBar().addOnMenuVisibilityListener(
                isVisible -> menuIsVisible[0] = isVisible);
        getInstrumentation().runOnMainSync(() -> mActivity.openOptionsMenu());

        assertTrue(menuIsVisible[0]);
        getInstrumentation().runOnMainSync(() -> mActivity.closeOptionsMenu());

        assertFalse(menuIsVisible[0]);
    }

    @Test
    public void testElevation() {
        if (mBar == null) {
            return;
        }
        final float oldElevation = mBar.getElevation();
        try {
            final float newElevation = 42;
            mBar.setElevation(newElevation);
            assertEquals(newElevation, mBar.getElevation());
        } finally {
            mBar.setElevation(oldElevation);
        }
    }

    private ActionBar.Tab createTab(String name) {
        return mBar.newTab().setText("Tab 1").setTabListener(new TestTabListener());
    }

    static class TestTabListener implements ActionBar.TabListener {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }
}
