package com.bigyoshi.qrhunt;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bigyoshi.qrhunt.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Definition: Builds app, manages fragments
 *
 *
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private Player player;
    private ImageButton navSearch, navProfile, mapMenu;
    private TextView scoreView;
    private FirebaseFirestore db;
    private DocumentReference playerRef;

    /**
     * OnCreate: sets up screen (toolbar, bottom menu), initializes player if need be
     * manages which fragment the app is in and adjusts accordingly, & passes things from
     * fragment to fragment
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get permissions first
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        });

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.top_nav);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);

        player = new Player(this);
        // This will check if the player already has an account
        if (!player.getPlayerId().matches("")){
            player.initialize();
        }

        scoreView = toolbar.findViewById(R.id.score_on_cam);
        updateFirebaseListeners();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navProfile = findViewById(R.id.navigation_profile);
        navProfile.setOnClickListener(view -> {
            binding.navView.setVisibility(View.INVISIBLE);
            FragmentProfile profile = new FragmentProfile(player, navController.getCurrentDestination().getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, profile, "profile");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        navSearch = findViewById(R.id.navigation_search);
        navSearch.setOnClickListener(view -> {});

        mapMenu = findViewById(R.id.map_list_button);
        mapMenu.setOnClickListener(view -> {});

        // determines current fragment so the right button is visible
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {

            if (navDestination.getId() == R.id.navigation_map) {
                actionbar.show();
                navSearch.setVisibility(View.GONE);
                mapMenu.setVisibility(View.VISIBLE);
                scoreView.setText("Map"); // May need to fix later
                navProfile.setVisibility(View.GONE); // TEMPORARY
            }
            if (navDestination.getId() == R.id.navigation_scanner) {
                actionbar.show();
                navSearch.setVisibility(View.VISIBLE);
                mapMenu.setVisibility(View.GONE);
                String scoreText = "Score: " + Integer.toString(player.getTotalScore()); // NEED TO UPDATE
                scoreView.setText(scoreText);
                navProfile.setVisibility(View.VISIBLE);
            }
            if (navDestination.getId() == R.id.navigation_rankBoard) {
                actionbar.hide();
                binding.navView.setVisibility(View.INVISIBLE);
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
            mapMenu.setVisibility(View.VISIBLE);
            scoreView.setText("Map"); // May need to fix later
            // Need to figure out how to go to the Map -> currently goes to Scanner (start dest)
            // For now I made this invisible
        } else if (prevFrag == R.id.navigation_scanner){
            actionbar.show();
            navSearch.setVisibility(View.VISIBLE);
            mapMenu.setVisibility(View.GONE);
            String scoreText = "Score: " + Integer.toString(player.getTotalScore());
            scoreView.setText(scoreText);
        }
    }

    /*
    This should be called whenever the user-id changes (eg, player transfers account)
     so that the proper id is is listened to for changes
     */
    /**
     * updates the database when score is updated
     *
     */
    private void updateFirebaseListeners() {
        // watch the score
        // this method should be called whenever userid changes, but only once
        playerRef = db.collection("users").document(player.getPlayerId());
        playerRef.addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) {
                Log.w(TAG, "Listening for score update has failed.", error);
                return;
            }
            String scoreVal = String.valueOf(snapshot.getData().getOrDefault("totalScore", 0));
            Log.d(TAG, String.format("set the score to be %s", scoreVal));
            scoreView.setText(String.format("Score: %s", scoreVal));
        });
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     *
     * @param permissions
     */
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}