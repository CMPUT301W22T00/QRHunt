package com.bigyoshi.qrhunt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.bigyoshi.qrhunt.player.Player;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FragmentSearchTest {
    private Solo solo;
    private Player mockPlayer;
    private ListView search;

    public void goToSearchBar(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_search));
        solo.waitForFragmentById(R.id.search_bar);
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
    public void checkGoToSearchFromScanner(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        goToSearchBar();
        assertNotNull(solo.getView(R.id.search_bar));
    }

    @Test
    public void checkGoBackToScanner(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        checkGoToSearchFromScanner();
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.search_bar_back_button));
        assertNotNull(solo.getView(R.id.scanner_view));
    }

    @Test
    public void checkSearchExistingUsername(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        checkGoToSearchFromScanner();
        EditText mockUserTyped = solo.getCurrentActivity().findViewById(R.id.search_bar_search_edit_text);
        mockPlayer = new Player("TEST-T00", "TEST-TOO", solo.getCurrentActivity().getBaseContext());
        search = solo.getCurrentActivity().findViewById(R.id.search_bar_results_list);
        solo.enterText(mockUserTyped, mockPlayer.getUsername());
        solo.waitForText(mockPlayer.getUsername());
        assertEquals(mockPlayer.getPlayerId(), ((Player) search.getItemAtPosition(0)).getPlayerId());
    }

    @Test
    public void checkSearchNonExistingUsername(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        checkGoToSearchFromScanner();
        EditText mockUserTyped = solo.getCurrentActivity().findViewById(R.id.search_bar_search_edit_text);
        solo.enterText(mockUserTyped, "TEST-T01");
        search = solo.getCurrentActivity().findViewById(R.id.search_bar_results_list);
        solo.waitForText("TEST-T01");
        assertEquals(0, search.getCount());
    }

    @Test
    public void checkGoToOtherPlayerProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        checkSearchExistingUsername();
        solo.clickInList(0);
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));
    }

    @Test
    public void checkFromProfileGoBackToSearch(){
        checkGoToOtherPlayerProfile();
        solo.goBack();
        solo.waitForFragmentById(R.id.search_bar);
        assertNotNull(solo.getView(R.id.search_bar));
    }

    @Test
    public void checkGearPresent(){
        Player player = ((MainActivity) solo.getCurrentActivity()).getPlayer();
        if (!player.isAdmin()){
            player.setAdmin(true);
        }
        checkGoToOtherPlayerProfile();
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button));
    }

    @Test
    public void checkGearAbsent(){
        Player player = ((MainActivity) solo.getCurrentActivity()).getPlayer();
        if (player.isAdmin()){
            player.setAdmin(false);
        }
        checkGoToOtherPlayerProfile();
        ImageButton button = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button);
        assertEquals(View.INVISIBLE, button.getVisibility());
    }

    @Test
    public void checkGearAbsentWhenSearchSelf(){
        Player player = ((MainActivity) solo.getCurrentActivity()).getPlayer();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        checkGoToSearchFromScanner();
        EditText mockUserTyped = solo.getCurrentActivity().findViewById(R.id.search_bar_search_edit_text);
        solo.enterText(mockUserTyped, player.getUsername());
        search = solo.getCurrentActivity().findViewById(R.id.search_bar_results_list);
        solo.waitForText(player.getUsername());
        solo.clickInList(0);
        solo.waitForFragmentById(R.id.player_profile);
        assertNotNull(solo.getView(R.id.player_profile));
        ImageButton button = solo.getCurrentActivity().findViewById(R.id.player_profile_settings_button);
        assertEquals(View.INVISIBLE, button.getVisibility());
    }

    @Test
    public void checkDelete(){
        checkGearPresent();
        solo.clickOnText("Delete Account");
        solo.clickOnButton("OK");
        solo.clickOnButton("OK");
        checkSearchNonExistingUsername();
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
