package com.bigyoshi.qrhunt;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FragmentPlayerProfileSettingTest {

    private Solo solo;

    public void goToPlayerProfileSetting(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_profile));
        solo.waitForFragmentById(R.id.player_profile);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
        solo.waitForFragmentById(R.id.player_settings);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_settings_edit_profile_clickable));
        solo.waitForFragmentById(R.id.player_profile_settings);
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
    public void checkAcceptsUsernameChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_username);
        solo.enterText(mockTyping, "HelloUser");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        Assert.assertEquals(mockText, "HelloUser");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel

    @Test
    public void checkAcceptsEmailChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_email);
        solo.enterText(mockTyping, "HelloEmail");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        Assert.assertEquals(mockText, "HelloEmail");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel

    @Test
    public void checkAcceptsSocialChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_social);
        solo.enterText(mockTyping, "HelloSocial");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        Assert.assertEquals(mockText, "HelloSocial");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel

    @Test
    public void checkAcceptsUsernameEmailChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_username);
        EditText mockTyping2 = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_email);
        solo.enterText(mockTyping, "HelloUser2");
        solo.enterText(mockTyping2, "HelloEmail2");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        String mockText2 = mockTyping2.getText().toString();
        Assert.assertEquals(mockText, "HelloUser2");
        Assert.assertEquals(mockText2, "HelloEmail2");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel

    @Test
    public void checkAcceptsUsernameSocialChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_username);
        EditText mockTyping2 = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_social);
        solo.enterText(mockTyping, "HelloUser3");
        solo.enterText(mockTyping2, "HelloSocial2");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        String mockText2 = mockTyping2.getText().toString();
        Assert.assertEquals(mockText, "HelloUser3");
        Assert.assertEquals(mockText2, "HelloSocial2");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel

    @Test
    public void checkAcceptsEmailSocialChange(){
        goToPlayerProfileSetting();
        EditText mockTyping = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_email);
        EditText mockTyping2 = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_edit_social);
        solo.enterText(mockTyping, "HelloEmail4");
        solo.enterText(mockTyping2, "HelloSocial3");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_ok_button));
        String mockText = mockTyping.getText().toString();
        String mockText2 = mockTyping2.getText().toString();
        Assert.assertEquals(mockText, "HelloEmail4");
        Assert.assertEquals(mockText2, "HelloSocial3");
        // Need to check if it updates for the player side
    }

    // Need to check if it doesn't updates for the player side -- pressing cancel
}
