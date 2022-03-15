package com.bigyoshi.qrhunt;

import android.app.Activity;
import android.widget.EditText;

import static org.junit.Assert.assertNotNull;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    private Solo solo;

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
    public void checkSentToPlayerProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_profile));
        solo.waitForFragmentById(R.id.playerProfile);
        assertNotNull(solo.getView(R.id.playerProfile));
    }

    @Test
    public void checkSentToMap(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_map));
        solo.waitForFragmentById(R.id.map);
        assertNotNull(solo.getView(R.id.map));
    }

    @Test
    public void checkSentToLeaderBoard(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_rankBoard));
        solo.waitForFragmentById(R.id.leaderboard);
        assertNotNull(solo.getView(R.id.leaderboard));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // Search button (NOT IMPLEMENTED)


    // MapList in top bar of Map (NOT IMPLEMENTED)

    // Check score is the same as in PlayerDB

    // Check player is initialized possibly
}
