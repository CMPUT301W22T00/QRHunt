package com.bigyoshi.qrhunt;

import static org.junit.Assert.assertNotNull;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FragmentProfileTest {

    private Solo solo;

    public void goToProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_profile));
        solo.waitForFragmentById(R.id.player_profile);
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
    public void checkSentToSettings(){
        goToProfile();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
        solo.waitForFragmentById(R.id.player_settings);
        assertNotNull(solo.getView(R.id.player_settings));
    }

    @Test
    public void checkSettingsToProfile(){
        goToProfile();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
        solo.waitForFragmentById(R.id.player_settings);
        assertNotNull(solo.getView(R.id.player_settings));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_back_button));
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));
    }

    @Test
    public void checkSocialButton(){
        goToProfile();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_contact_button));
    }

    // Check if SocialButton is not clickable (nothing seen)
    // Check if SocialButton shows contacts of player
    // Check if call out when you click out of the social button

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
