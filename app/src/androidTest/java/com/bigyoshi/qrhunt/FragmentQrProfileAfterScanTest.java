package com.bigyoshi.qrhunt;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.bigyoshi.qrhunt.qr.FragmentQrProfileAfterScan;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FragmentQrProfileAfterScanTest {

    private Solo solo;

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(FragmentQrProfileAfterScan.class,
            true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    // Need to test the bottom navigation
    @Test
    public void checkClickTakePhoto() {

    }




    // Need to test the top bar navigation
}
