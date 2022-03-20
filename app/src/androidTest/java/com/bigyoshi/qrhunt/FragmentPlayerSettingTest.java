package com.bigyoshi.qrhunt;

import static org.junit.Assert.assertNotNull;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FragmentPlayerSettingTest {

    private Solo solo;

    public void goToProfileSetting(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_profile));
        solo.waitForFragmentById(R.id.playerProfile);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.profile_settings_button));
        solo.waitForFragmentById(R.id.playerSettings);
    }

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(MainActivity.class,
            true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSentToProfileSettings(){
        goToProfileSetting();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.settings_edit_profile));
        solo.waitForFragmentById(R.id.editPlayerProfile);
        assertNotNull(solo.getView(R.id.editPlayerProfile));
    }

    @Test
    public void checkProfileSettingsBackToSettingsCancel(){
        goToProfileSetting();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.settings_edit_profile));
        solo.waitForFragmentById(R.id.editPlayerProfile);
        assertNotNull(solo.getView(R.id.editPlayerProfile));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.cancel_edit_profile));
        assertNotNull(solo.getView(R.id.playerSettings));
    }

    @Test
    public void checkProfileSettingsBackToSettingsOk(){
        goToProfileSetting();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.settings_edit_profile));
        solo.waitForFragmentById(R.id.editPlayerProfile);
        assertNotNull(solo.getView(R.id.editPlayerProfile));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.ok_edit_profile));
        assertNotNull(solo.getView(R.id.playerSettings));
    }

    // Need to test press on QR when set up
}
