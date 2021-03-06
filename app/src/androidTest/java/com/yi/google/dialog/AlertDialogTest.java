package com.yi.google.dialog;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.yi.google.R;
import com.yi.google.util.PollingCheck;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AlertDialogTest {
    private Instrumentation mInstrumentation;
    private ActivityScenario<DialogStubActivity> mScenario;
    private DialogStubActivity mActivity;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private Button mNeutralButton;

    @Before
    public void setUp() throws Exception {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @After
    public void tearDown() {
        if (mScenario != null) {
            mScenario.close();
            mScenario = null;
        }
    }

    protected void startDialogActivity(int dialogNumber) {
        mScenario = DialogStubActivity.startDialogActivity(mInstrumentation.getTargetContext(), dialogNumber);
        mScenario.onActivity(activity -> {
            mActivity = activity;
        });
        PollingCheck.waitFor(mActivity.getDialog()::isShowing);
        PollingCheck.waitFor(mActivity.getDialog().getWindow().getDecorView()::hasWindowFocus);
    }

    @Test
    public void testAlertDialog()throws Throwable{
        doTestAlertDialog(DialogStubActivity.TEST_ALERTDIALOG);
    }

    private void doTestAlertDialog(int index) throws Throwable {
        startDialogActivity(index);
        assertTrue(mActivity.getDialog().isShowing());

        mPositiveButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_POSITIVE);
        assertNotNull(mPositiveButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_positive),
                mPositiveButton.getText());
        mNeutralButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_NEUTRAL);
        assertNotNull(mNeutralButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_neutral),
                mNeutralButton.getText());
        mNegativeButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_NEGATIVE);
        assertNotNull(mNegativeButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_negative),
                mNegativeButton.getText());

        assertFalse(mActivity.isPositiveButtonClicked);
        performClick(mPositiveButton);
        PollingCheck.waitFor(() -> mActivity.isPositiveButtonClicked);

        assertFalse(mActivity.isNegativeButtonClicked);
        performClick(mNegativeButton);
        PollingCheck.waitFor(() -> mActivity.isNegativeButtonClicked);

        assertFalse(mActivity.isNeutralButtonClicked);
        performClick(mNeutralButton);
        PollingCheck.waitFor(() -> mActivity.isNeutralButtonClicked);
    }

    @Test
    public void testAlertDialogDeprecatedAPI() throws Throwable{
        doTestAlertDialog(DialogStubActivity.TEST_ALERTDIALOG_DEPRECATED);
    }

    @Test
    public void testAlertDialogAPIWithMessageDeprecated() throws Throwable {
        testAlertDialogAPIWithMessage(true);
    }

    @Test
    public void testAlertDialogAPIWithMessageNotDeprecated() throws Throwable {
        testAlertDialogAPIWithMessage(false);
    }

    private void testAlertDialogAPIWithMessage(final boolean useDeprecatedAPIs) throws Throwable {
        startDialogActivity(useDeprecatedAPIs
                ? DialogStubActivity.TEST_ALERTDIALOG_DEPRECATED_WITH_MESSAGE
                : DialogStubActivity.TEST_ALERTDIALOG_WITH_MESSAGE);

        assertTrue(mActivity.getDialog().isShowing());

        mPositiveButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_POSITIVE);
        assertNotNull(mPositiveButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_positive),
                mPositiveButton.getText());
        mNegativeButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_NEGATIVE);
        assertNotNull(mNegativeButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_negative),
                mNegativeButton.getText());
        mNeutralButton = ((AlertDialog) (mActivity.getDialog())).getButton(
                DialogInterface.BUTTON_NEUTRAL);
        assertNotNull(mNeutralButton);
        assertEquals(mActivity.getString(R.string.alert_dialog_neutral),
                mNeutralButton.getText());

        DialogStubActivity.buttonIndex = 0;
        performClick(mPositiveButton);
        PollingCheck.waitFor(() ->
                (DialogInterface.BUTTON_POSITIVE == DialogStubActivity.buttonIndex));

        DialogStubActivity.buttonIndex = 0;
        performClick(mNeutralButton);
        PollingCheck.waitFor(() ->
                (DialogInterface.BUTTON_NEUTRAL == DialogStubActivity.buttonIndex));

        DialogStubActivity.buttonIndex = 0;
        performClick(mNegativeButton);
        PollingCheck.waitFor(() ->
                (DialogInterface.BUTTON_NEGATIVE == DialogStubActivity.buttonIndex));
    }

    private void performClick(final Button button) throws Throwable {
        mScenario.onActivity(activity -> button.performClick());
    }


    @Test
    public void testCustomAlertDialog() {
        startDialogActivity(DialogStubActivity.TEST_CUSTOM_ALERTDIALOG);
        assertTrue(mActivity.getDialog().isShowing());
    }

    @Test
    public void testCustomAlertDialogView() {
        startDialogActivity(DialogStubActivity.TEST_CUSTOM_ALERTDIALOG_VIEW);
        assertTrue(mActivity.getDialog().isShowing());
    }

    @Test
    public void testCallback() {
        startDialogActivity(DialogStubActivity.TEST_ALERTDIALOG_CALLBACK);
        assertTrue(mActivity.onCreateCalled);

        mInstrumentation.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0));
        assertTrue(mActivity.onKeyDownCalled);
        mInstrumentation.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_0));
        assertTrue(mActivity.onKeyUpCalled);
    }

    @Test
    public void testAlertDialogTheme() {
        startDialogActivity(DialogStubActivity.TEST_ALERTDIALOG_THEME);
        assertTrue(mActivity.getDialog().isShowing());
    }

    @Test
    public void testAlertDialogCancelable() {
        startDialogActivity(DialogStubActivity.TEST_ALERTDIALOG_CANCELABLE);
        assertTrue(mActivity.getDialog().isShowing());
        assertFalse(mActivity.onCancelCalled);
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        PollingCheck.waitFor(() -> mActivity.onCancelCalled);
    }

    @Test
    public void testAlertDialogNotCancelable() {
        startDialogActivity(DialogStubActivity.TEST_ALERTDIALOG_NOT_CANCELABLE);
        assertTrue(mActivity.getDialog().isShowing());
        assertFalse(mActivity.onCancelCalled);
        mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
        assertFalse(mActivity.onCancelCalled);
    }

    @Test
    public void testAlertDialogIconDrawable() {
        startDialogActivity(DialogStubActivity.TEST_ALERT_DIALOG_ICON_DRAWABLE);
        assertTrue(mActivity.getDialog().isShowing());
    }

    @Test
    public void testAlertDialogIconAttribute() {
        startDialogActivity(DialogStubActivity.TEST_ALERT_DIALOG_ICON_ATTRIBUTE);
        assertTrue(mActivity.getDialog().isShowing());
    }


}
