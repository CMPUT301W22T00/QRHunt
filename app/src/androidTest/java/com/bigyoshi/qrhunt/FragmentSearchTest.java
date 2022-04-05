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

public class FragmentSearchTest {
    private Solo solo;

    public void gotoSearch(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.top_navigation_search));
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
    public void searchSuccess(){
        gotoSearch();
        EditText searchBar = solo.getEditText(0);
        solo.enterText(searchBar, "Accordio");

        solo.clickOnText("Accordion");
        solo.waitForFragmentById(R.id.player_profile);

    }

    @Test
    public void searchFail(){
        gotoSearch();
        EditText searchBar = solo.getEditText(0);
        solo.enterText(searchBar, "as of right now, this should not be a username in our system");

        Assert.assertTrue(solo.searchText("No results found"));

    }
}
