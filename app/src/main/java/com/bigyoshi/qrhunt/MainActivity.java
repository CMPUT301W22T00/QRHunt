package com.bigyoshi.qrhunt;


import static com.google.firebase.firestore.FieldPath.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bigyoshi.qrhunt.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private Player player;
    private ImageButton navSearch, navProfile, mapMenu;
    private TextView score;
    private FirebaseFirestore db;
    private DocumentReference playerRef;

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
        playerRef = db.collection("users").document("TEST USER");

        Toolbar toolbar = findViewById(R.id.top_nav);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);

        player = new Player(this);

        score = toolbar.findViewById(R.id.score_on_cam);
        updateFirebaseListeners();
        String scoreText = "Score: " + Integer.toString(player.getPlayerInfo().getQRTotal());
        score.setText(scoreText);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navProfile = findViewById(R.id.navigation_profile);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.navView.setVisibility(View.INVISIBLE);
                FragmentProfile profile = new FragmentProfile(player);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, profile, "profile");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        navSearch = findViewById(R.id.navigation_search);
        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        mapMenu = findViewById(R.id.map_list_button);
        mapMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        // determines current fragment so the right button is visible
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

                if (navDestination.getId() == R.id.navigation_map) {
                    navSearch.setVisibility(View.GONE);
                    mapMenu.setVisibility(View.VISIBLE);
                }
                if (navDestination.getId() == R.id.navigation_scanner) {
                    navSearch.setVisibility(View.VISIBLE);
                    mapMenu.setVisibility(View.GONE);

                }
            }
        });
    }

    /*
    This should be called whenever the user-id changes (eg, player transfers account)
     so that the proper id is is listened to for changes
     */
    private void updateFirebaseListeners() {
//        CollectionReference playerQrCodesRef = db.collection("users").document(player.getPlayerId()).collection("qrCodes");
        CollectionReference playerQrCodesRef = playerRef.collection("qrCodes");
        playerQrCodesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> qrIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    qrIds.add(document.getId());
                }
                Log.d(TAG, String.format("got %d ids", qrIds.size()));

                db.collection("qrCodes").whereIn(documentId(), qrIds).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int total = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            total += (int)document.getData().getOrDefault("score", 0);
                        }
                        Log.d(TAG, String.format("total score: %d", total));
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

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