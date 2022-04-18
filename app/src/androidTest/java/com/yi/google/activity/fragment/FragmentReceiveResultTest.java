package com.yi.google.activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.yi.google.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SmallTest
@RunWith(AndroidJUnit4.class)
public class FragmentReceiveResultTest {

    private FragmentTestActivity mActivity;
    private Fragment mFragment;

    @Rule
    public ActivityScenarioRule<FragmentTestActivity> mRule = new ActivityScenarioRule<>(FragmentTestActivity.class);

    @Before
    public void setUp() throws Exception {
        mRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });
        mFragment = attachTestFragment();
    }

    @Test
    public void testStartActivityForResultOk() {
        startActivityForResult(10, Activity.RESULT_OK, "content 10");

        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mFragment, times(1))
                .onActivityResult(eq(10), eq(Activity.RESULT_OK), captor.capture());
        final String data = captor.getValue()
                .getStringExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT);
        assertEquals("content 10", data);
    }

    @Test
    public void testStartActivityForResultCanceled() {
        startActivityForResult(20, Activity.RESULT_CANCELED, "content 20");

        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mFragment, times(1))
                .onActivityResult(eq(20), eq(Activity.RESULT_CANCELED), captor.capture());
        final String data = captor.getValue()
                .getStringExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT);
        assertEquals("content 20", data);
    }

    @Test
    public void testStartIntentSenderForResultOk() {
        startIntentSenderForResult(30, Activity.RESULT_OK, "content 30");

        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mFragment, times(1))
                .onActivityResult(eq(30), eq(Activity.RESULT_OK), captor.capture());
        final String data = captor.getValue()
                .getStringExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT);
        assertEquals("content 30", data);
    }

    @Test
    public void testStartIntentSenderForResultCanceled() {
        startIntentSenderForResult(40, Activity.RESULT_CANCELED, "content 40");

        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mFragment, times(1))
                .onActivityResult(eq(40), eq(Activity.RESULT_CANCELED), captor.capture());
        final String data = captor.getValue()
                .getStringExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT);
        assertEquals("content 40", data);
    }

    private Fragment attachTestFragment() {
        final Fragment fragment = spy(new Fragment());
        getInstrumentation().waitForIdleSync();
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mActivity.getFragmentManager().beginTransaction()
                        .add(R.id.content, fragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                mActivity.getFragmentManager().executePendingTransactions();
            }
        });
        getInstrumentation().waitForIdleSync();
        return fragment;
    }

    private void startActivityForResult(final int requestCode, final int resultCode,
                                        final String content) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mActivity, FragmentResultActivity.class);
                intent.putExtra(FragmentResultActivity.EXTRA_RESULT_CODE, resultCode);
                intent.putExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT, content);

                mFragment.startActivityForResult(intent, requestCode);
            }
        });
        getInstrumentation().waitForIdleSync();
    }

    private void startIntentSenderForResult(final int requestCode, final int resultCode,
                                            final String content) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mActivity, FragmentResultActivity.class);
                intent.putExtra(FragmentResultActivity.EXTRA_RESULT_CODE, resultCode);
                intent.putExtra(FragmentResultActivity.EXTRA_RESULT_CONTENT, content);

                PendingIntent pendingIntent = PendingIntent.getActivity(mActivity,
                        requestCode, intent, 0);

                try {
                    mFragment.startIntentSenderForResult(pendingIntent.getIntentSender(),
                            requestCode, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    fail("IntentSender failed");
                }
            }
        });
        getInstrumentation().waitForIdleSync();
    }

}
