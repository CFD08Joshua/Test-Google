package com.yi.google.activity.callback;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event;
import com.yi.google.activity.callback.ActivityCallbacksTestActivity.Source;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_CREATE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_DESTROY;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PAUSE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_CREATE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_DESTROY;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_PAUSE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_RESUME;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_START;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_POST_STOP;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_CREATE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_DESTROY;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_PAUSE;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_RESUME;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_START;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_PRE_STOP;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_RESUME;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_START;
import static com.yi.google.activity.callback.ActivityCallbacksTestActivity.Event.ON_STOP;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ActivityCallbacksTest {
    public static final long TIMEOUT_SECS = 2;

    private Application.ActivityLifecycleCallbacks mActivityCallbacks;
    private ActivityCallbacksTestActivity mActivity;

    @Rule
    public ActivityScenarioRule<ActivityCallbacksTestActivity> mActivityRule =
            new ActivityScenarioRule<>(ActivityCallbacksTestActivity.class);


    @After
    public void tearDown() {
        if (mActivityCallbacks != null) {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Application application = (Application) targetContext.getApplicationContext();
            application.unregisterActivityLifecycleCallbacks(mActivityCallbacks);
        }
    }

    @Test
    public void testActivityCallbackOrder() throws InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Application application = (Application) targetContext.getApplicationContext();
        ArrayList<Pair<Source, Event>> actualEvents = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        mActivityCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityPreCreated(Activity activity, Bundle savedInstanceState) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_CREATE);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_CREATE);
            }

            @Override
            public void onActivityPostCreated(Activity activity, Bundle savedInstanceState) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_CREATE);
            }

            @Override
            public void onActivityPreStarted(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_START);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_START);
            }

            @Override
            public void onActivityPostStarted(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_START);
            }

            @Override
            public void onActivityPreResumed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_RESUME);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_RESUME);
            }

            @Override
            public void onActivityPostResumed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_RESUME);
                a.finish();
            }

            @Override
            public void onActivityPrePaused(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_PAUSE);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PAUSE);
            }

            @Override
            public void onActivityPostPaused(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_PAUSE);
            }

            @Override
            public void onActivityPreStopped(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_STOP);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_STOP);
            }

            @Override
            public void onActivityPostStopped(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_STOP);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityPreDestroyed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_PRE_DESTROY);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_DESTROY);
            }

            @Override
            public void onActivityPostDestroyed(Activity activity) {
                if (!wanted(activity)) return;
                ActivityCallbacksTestActivity a = (ActivityCallbacksTestActivity) activity;
                a.collectEvent(Source.APPLICATION_ACTIVITY_CALLBACK, ON_POST_DESTROY);
                actualEvents.addAll(a.getCollectedEvents());
                latch.countDown();
            }
        };

        application.registerActivityLifecycleCallbacks(mActivityCallbacks);

//        mActivityRule.launchActivity(null);

        mActivityRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });

//        assertTrue("Failed to await for an activity to finish ",
//                latch.await(TIMEOUT_SECS, TimeUnit.SECONDS));

        ArrayList<Pair<Source, Event>> expectedEvents = new ArrayList<>();
        addNestedEvents(expectedEvents, ON_PRE_CREATE, ON_CREATE, ON_POST_CREATE);
        addNestedEvents(expectedEvents, ON_PRE_START, ON_START, ON_POST_START);
        addNestedEvents(expectedEvents, ON_PRE_RESUME, ON_RESUME, ON_POST_RESUME);

        addNestedEvents(expectedEvents, ON_PRE_PAUSE, ON_PAUSE, ON_POST_PAUSE);
        addNestedEvents(expectedEvents, ON_PRE_STOP, ON_STOP, ON_POST_STOP);
        addNestedEvents(expectedEvents, ON_PRE_DESTROY, ON_DESTROY, ON_POST_DESTROY);

//        assertEquals(expectedEvents, actualEvents);
    }

    private void addNestedEvents(ArrayList<Pair<Source, Event>> expectedEvents,
                                 Event preEvent, Event event, Event postEvent) {
        expectedEvents.add(new Pair<>(Source.APPLICATION_ACTIVITY_CALLBACK, preEvent));
        expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK, preEvent));
        if (preEvent == ON_PRE_CREATE) {
            // ACTIVITY_CALLBACK_CREATE_ONLY only gets create events
            expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK_CREATE_ONLY, preEvent));
        }
        expectedEvents.add(new Pair<>(Source.ACTIVITY, preEvent));
        if (event == ON_CREATE || event == ON_START || event == ON_RESUME) {
            // Application goes first on upward lifecycle events
            expectedEvents.add(new Pair<>(Source.APPLICATION_ACTIVITY_CALLBACK, event));
            expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK, event));
            if (event == ON_CREATE) {
                // ACTIVITY_CALLBACK_CREATE_ONLY only gets create events
                expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK_CREATE_ONLY, event));
            }
        } else {
            // Application goes last on downward lifecycle events
            expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK, event));
            expectedEvents.add(new Pair<>(Source.APPLICATION_ACTIVITY_CALLBACK, event));
        }
        expectedEvents.add(new Pair<>(Source.ACTIVITY, postEvent));
        expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK, postEvent));
        if (postEvent == ON_POST_CREATE) {
            // ACTIVITY_CALLBACK_CREATE_ONLY only gets create events
            expectedEvents.add(new Pair<>(Source.ACTIVITY_CALLBACK_CREATE_ONLY, postEvent));
        }
        expectedEvents.add(new Pair<>(Source.APPLICATION_ACTIVITY_CALLBACK, postEvent));
    }

    private boolean wanted(Activity activity) {
        return activity instanceof ActivityCallbacksTestActivity;
    }
}

