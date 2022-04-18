package com.yi.google.activity.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * A simple Activity used to return a result.
 */
public class FragmentResultActivity extends Activity {
    public static final String EXTRA_RESULT_CODE = "result";
    public static final String EXTRA_RESULT_CONTENT = "result_content";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        int resultCode = getIntent().getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_OK);
        String result = getIntent().getStringExtra(EXTRA_RESULT_CONTENT);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_CONTENT, result);
        setResult(resultCode, intent);
        finish();
    }
}
