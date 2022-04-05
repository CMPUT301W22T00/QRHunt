package com.bigyoshi.qrhunt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

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
        try {
            solo.getCurrentActivity().findViewById(R.id.contact_call_out);
        } catch(AssertionFailedError e ){
            Log.d(FragmentProfileTest.class.getSimpleName(), "checkSocialButton: " + e.getMessage());
        }

        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
        solo.waitForFragmentById(R.id.player_settings);
        assertNotNull(solo.getView(R.id.player_settings));

        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_edit_profile_clickable));
        solo.waitForFragmentById(R.id.player_settings_edit_profile);
        assertNotNull(solo.getView(R.id.player_settings_edit_profile));

        EditText mockTyping = solo.getEditText(1);
        EditText mockTyping2 = solo.getEditText(2);
        solo.enterText(mockTyping, "HelloEmail4");
        solo.enterText(mockTyping2, "HelloSocial3");

        solo.clickOnButton("Save");
        solo.waitForFragmentById(R.id.player_settings);

        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_back_button));
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));

        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_contact_button));

        assertTrue(solo.searchText("HelloEmail4"));
        assertTrue(solo.searchText("HelloSocial3"));

        solo.clickOnText("Total Score Rank");

        assertFalse(solo.searchText("HelloEmail4"));
        assertFalse(solo.searchText("HelloSocial3"));

    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
