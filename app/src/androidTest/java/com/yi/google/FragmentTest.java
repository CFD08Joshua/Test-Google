package com.yi.google;

import android.app.Fragment;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@SmallTest
public class FragmentTest {

    @Before
    public void setUp() throws Exception {

    }

    public static class TestFragment extends Fragment {
        public TestFragment() {}
    }

    public static class TestNotFragment {
        public TestNotFragment() {
            throw new IllegalStateException("Shouldn't call constructor");
        }
    }
    @Test
    public void testInstantiateFragment() {
        assertNotNull(Fragment.instantiate(getContext(), TestFragment.class.getName()));
    }
    @Test
    public void testInstantiateNonFragment() {
        try {
            Fragment.instantiate(getContext(), TestNotFragment.class.getName());
            fail();
        } catch (Exception e) {
            // Should get an exception and it shouldn't be an IllegalStateException
            assertFalse(e instanceof IllegalStateException);
        }
    }
}
