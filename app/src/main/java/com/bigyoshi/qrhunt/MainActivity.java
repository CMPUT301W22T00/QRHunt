package com.bigyoshi.qrhunt;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bigyoshi.qrhunt.bottom_navigation.search.FragmentSearch;
import com.bigyoshi.qrhunt.databinding.ActivityMainBinding;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.bigyoshi.qrhunt.player.SelfPlayer;
import com.bigyoshi.qrhunt.qr.FragmentScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Definition: Builds app, manages fragments, and accesses database
 * Note: Mainly controls the bottom navigation and top navigation bars
 * Issues: When the player first installs the app and creates an account, the app freezes (need to restart app to play)
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private Player player;
    private ImageButton navSearch, navProfile;
    private TextView scoreView;
    private FirebaseFirestore db;
    private DocumentReference playerRef;

    /**
     * Sets up screen (toolbar, bottom menu), initializes player if need be
     * manages which fragment the app is in and adjusts accordingly, & passes things from
     * fragment to fragment
     *
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.top_navigation_view);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);

        player = new SelfPlayer(this);
        // This will check if the player already has an account
        if (player.getPlayerId().matches(db.collection("users").document(player.getPlayerId()).getId())){
            player.initialize();
        } else {
            player.setPlayerId(player.getPlayerId()); // save it
        }

        scoreView = toolbar.findViewById(R.id.top_scanner_score);
        updateFirebaseListeners();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this,
                R.id.main_bottom_navigation_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        // todo: find a way to pass playerId to the leaderbaord throuhg this
        //  duart forsaken interface that is frag nav
        // findViewById(R.id.navigation_leaderBoard).setOnClickListener(__ -> {
        //  Bundle res = new Bundle();
        //  res.putString("playerId", player.getPlayerId());
        //  Navigation.findNavController(binding.getRoot()).navigate(R.id.navigation_leaderBoard, res);
        // });

        navProfile = findViewById(R.id.top_navigation_profile);
        navProfile.setOnClickListener(view -> {
            binding.bottomNavigationView.setVisibility(View.INVISIBLE);

            FragmentProfile profile = new FragmentProfile();

            Bundle bundle = new Bundle();
            bundle.putSerializable("player", player);
            bundle.putSerializable("selfPlayer", player);
            bundle.putSerializable("isActivity", 1);
            bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.OWN_VIEW);
            profile.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, profile, "profile");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        navSearch = findViewById(R.id.top_navigation_search);
        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.bottomNavigationView.setVisibility(View.INVISIBLE);
                actionbar.hide();
                FragmentSearch search = new FragmentSearch(player,
                        navController.getCurrentDestination().getId());

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, search, "search");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                FragmentScanner.codeScanner.setScanMode(ScanMode.PREVIEW);
            }
        });



        // determines current fragment so the right button is visible
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.navigation_map) {
                actionbar.show();
                Bundle result = new Bundle();
                result.putSerializable("player", player);
                getSupportFragmentManager().setFragmentResult("getPlayer", result);
                navSearch.setVisibility(View.GONE);
                navProfile.setVisibility(View.GONE); // TEMPORARY
            }
            if (navDestination.getId() == R.id.navigation_scanner) {
                actionbar.show();
                Bundle result = new Bundle();
                result.putSerializable("player", player);
                getSupportFragmentManager().setFragmentResult("getPlayer", result);
                navSearch.setVisibility(View.VISIBLE);
                navProfile.setVisibility(View.VISIBLE);
            }
            if (navDestination.getId() == R.id.navigation_leaderBoard) {
                actionbar.hide();
                binding.bottomNavigationView.setVisibility(View.INVISIBLE);
            }
        });

        Intent intent = this.getIntent();
        Bundle s = intent.getExtras();
        int prevFrag = -1;
        if (s != null) {
            prevFrag = (int) s.getSerializable("previous");
        }
        if (prevFrag == R.id.navigation_map) {
            actionbar.show();
            navSearch.setVisibility(View.GONE);
            // Need to figure out how to go to the Map -> currently goes to Scanner (start dest)
            // For now I made this invisible
        } else if (prevFrag == R.id.navigation_scanner){
            actionbar.show();
            navSearch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the database when score is updated
     * Note: Should be called whenever the playerId changes (transfers account) - proper id listened for changes
     *
     */
    private void updateFirebaseListeners() {
        // watch the score
        // this method should be called whenever userid changes, but only once
        playerRef = db.collection("users").document(player.getPlayerId());
        playerRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null || !snapshot.exists()) {
                Log.w(TAG, "Listening for score update has failed.", error);
                return;
            }
            String scoreVal = String.valueOf(snapshot.getData().getOrDefault("totalScore", 0));
            Log.d(TAG, String.format("set the score to be %s", scoreVal));
            scoreView.setText(String.format("Score: %s", scoreVal));
        });
    }

}