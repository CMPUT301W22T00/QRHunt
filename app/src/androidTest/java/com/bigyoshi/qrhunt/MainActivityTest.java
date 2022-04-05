package com.bigyoshi.qrhunt;

import android.app.Activity;

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
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_profile));
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));
    }

    @Test
    public void checkProfileToScanner(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_profile));
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));
        solo.goBack();
        solo.waitForFragmentById(R.id.scanner_view);
        assertNotNull(solo.getView(R.id.scanner_view));
    }


    @Test
    public void checkSentToSearch(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_search));
        solo.waitForFragmentById(R.id.search_bar);
        assertNotNull(solo.getView(R.id.search_bar));
    }

    //Need a sent back to Scanner / Sent back to Map from Search





    @Test
    public void checkSentToMap(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_map));
        solo.waitForFragmentById(R.id.map);
        assertNotNull(solo.getView(R.id.map));
    }

    @Test
    public void checkMapToScanner(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_map));
        solo.waitForFragmentById(R.id.map);
        assertNotNull(solo.getView(R.id.map));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_scanner));
        solo.waitForFragmentById(R.id.scanner_view);
        assertNotNull(solo.getView(R.id.scanner_view));
    }

    @Test
    public void checkMapToLeaderboard(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_map));
        solo.waitForFragmentById(R.id.map);
        assertNotNull(solo.getView(R.id.map));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_leaderBoard));
        solo.waitForFragmentById(R.id.leaderboard);
        assertNotNull(solo.getView(R.id.leaderboard));
    }

    @Test
    public void checkSentToLeaderBoard(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_leaderBoard));
        solo.waitForFragmentById(R.id.leaderboard);
        assertNotNull(solo.getView(R.id.leaderboard));
    }

    @Test
    public void checkLeaderBoardToScanner(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.navigation_leaderBoard));
        solo.waitForFragmentById(R.id.leaderboard);
        assertNotNull(solo.getView(R.id.leaderboard));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.leaderboard_back_button));
        solo.waitForFragmentById(R.id.scanner_view);
        assertNotNull(solo.getView(R.id.scanner_view));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
