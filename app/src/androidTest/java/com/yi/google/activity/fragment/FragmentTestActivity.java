package com.yi.google.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.yi.google.R;

public class FragmentTestActivity extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_content);
        // Work around problems running while on lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static class TestFragment extends Fragment {
        public static final int ENTER = 0;
        public static final int RETURN = 1;
        public static final int EXIT = 2;
        public static final int REENTER = 3;
        public static final int SHARED_ELEMENT_ENTER = 4;
        public static final int SHARED_ELEMENT_RETURN = 5;
        private static final int TRANSITION_COUNT = 6;

        private static final String LAYOUT_ID = "layoutId";
        private static final String TRANSITION_KEY = "transition_";
        private int mLayoutId = R.layout.fragment_start;
        private final int[] mTransitionIds = new int[] {
                android.R.transition.explode,
                android.R.transition.explode,
                android.R.transition.fade,
                android.R.transition.fade,
                android.R.transition.move,
                android.R.transition.move,
        };
        private final TransitionCalledListener[] mListeners =
                new TransitionCalledListener[TRANSITION_COUNT];
        private OnTransitionListener mOnTransitionListener;

        public TestFragment() {
            for (int i = 0; i < TRANSITION_COUNT; i++) {
                mListeners[i] = new TransitionCalledListener();
            }
        }

        public TestFragment(int layoutId) {
            this();
            mLayoutId = layoutId;
        }

        public void clearTransitions() {
            for (int i = 0; i < TRANSITION_COUNT; i++) {
                mTransitionIds[i] = 0;
            }
        }

        public void clearNotifications() {
            for (int i = 0; i < TRANSITION_COUNT; i++) {
                mListeners[i].transitionStarted = false;
                mListeners[i].transitionEnded = false;
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                mLayoutId = savedInstanceState.getInt(LAYOUT_ID, mLayoutId);
                for (int i = 0; i < TRANSITION_COUNT; i++) {
                    String key = TRANSITION_KEY + i;
                    mTransitionIds[i] = savedInstanceState.getInt(key, mTransitionIds[i]);
                }
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(LAYOUT_ID, mLayoutId);
            for (int i = 0; i < TRANSITION_COUNT; i++) {
                String key = TRANSITION_KEY + i;
                outState.putInt(key, mTransitionIds[i]);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(mLayoutId, container, false);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            setEnterTransition(loadTransition(ENTER));
            setReenterTransition(loadTransition(REENTER));
            setExitTransition(loadTransition(EXIT));
            setReturnTransition(loadTransition(RETURN));
            setSharedElementEnterTransition(loadTransition(SHARED_ELEMENT_ENTER));
            setSharedElementReturnTransition(loadTransition(SHARED_ELEMENT_RETURN));
        }

        public void setOnTransitionListener(OnTransitionListener listener) {
            mOnTransitionListener = listener;
        }

        public boolean wasStartCalled(int transitionKey) {
            return mListeners[transitionKey].transitionStarted;
        }

        public boolean wasEndCalled(int transitionKey) {
            return mListeners[transitionKey].transitionEnded;
        }

        private Transition loadTransition(int key) {
            final int id = mTransitionIds[key];
            if (id == 0) {
                return null;
            }
            Transition transition = TransitionInflater.from(getActivity()).inflateTransition(id);
            transition.addListener(mListeners[key]);
            return transition;
        }

        private void notifyTransition() {
            if (mOnTransitionListener != null) {
                mOnTransitionListener.onTransition(this);
            }
        }

        private class TransitionCalledListener implements Transition.TransitionListener {
            public boolean transitionStarted;
            public boolean transitionEnded;

            public TransitionCalledListener() {
            }

            @Override
            public void onTransitionStart(Transition transition) {
                transitionStarted = true;
                notifyTransition();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transitionEnded = true;
                notifyTransition();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        }
    }

    public interface OnTransitionListener {
        void onTransition(TestFragment fragment);
    }
}
