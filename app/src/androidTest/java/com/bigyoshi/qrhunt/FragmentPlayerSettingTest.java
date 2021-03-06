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
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_profile));
        solo.waitForFragmentById(R.id.player_profile);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
        solo.waitForFragmentById(R.id.player_settings);
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
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_edit_profile_clickable));
        solo.waitForFragmentById(R.id.player_profile_settings);
        assertNotNull(solo.getView(R.id.player_profile_settings));
    }

    @Test
    public void checkProfileSettingsBackToSettingsCancel(){
        goToProfileSetting();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_edit_profile_clickable));
        solo.waitForFragmentById(R.id.player_profile_settings);
        assertNotNull(solo.getView(R.id.player_profile_settings));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_cancel_button));
        assertNotNull(solo.getView(R.id.player_settings));
    }

    @Test
    public void checkProfileSettingsBackToSettingsOk(){
        goToProfileSetting();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_edit_profile_clickable));
        solo.waitForFragmentById(R.id.player_profile_settings);
        assertNotNull(solo.getView(R.id.player_profile_settings));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        assertNotNull(solo.getView(R.id.player_settings));
    }

    // Need to test press on QR when set up
}
