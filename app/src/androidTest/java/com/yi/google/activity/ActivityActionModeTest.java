package com.yi.google.activity;

import android.view.ActionMode;
import android.view.Window;

import androidx.annotation.UiThread;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ActivityActionModeTest {
    private ActionMode.Callback mCallback;
    private MockActivity mActivity;
    @Rule
    public ActivityScenarioRule<MockActivity> mActivityRule = new ActivityScenarioRule<>(MockActivity.class);

    @Before
    public void setUp() throws Exception{
        mActivityRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });
        mCallback = mock(ActionMode.Callback.class);
        when(mCallback.onCreateActionMode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        when(mCallback.onPrepareActionMode(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
    }
    
    @Test
    public void testStartPrimaryActionMode(){
        if (!mActivity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR)) {
            return;
        }

        final ActionMode mode = mActivity.startActionMode(
                mCallback, ActionMode.TYPE_PRIMARY);

        assertNotNull(mode);
        assertEquals(ActionMode.TYPE_PRIMARY, mode.getType());
    }

    @Test
    public void testStartFloatingActionMode() {
        if (!mActivity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR)) {
            return;
        }

        final ActionMode mode = mActivity.startActionMode(
                mCallback, ActionMode.TYPE_FLOATING);

        assertNotNull(mode);
        assertEquals(ActionMode.TYPE_FLOATING, mode.getType());
    }

    @Test
    @UiThread
    public void testStartTypelessActionMode() {
        if (!mActivity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR)) {
            return;
        }

        final ActionMode mode = mActivity.startActionMode(mCallback);

        assertNotNull(mode);
        assertEquals(ActionMode.TYPE_PRIMARY, mode.getType());
    }
}
