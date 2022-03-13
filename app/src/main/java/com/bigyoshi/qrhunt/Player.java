package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class Player {
    private static final String TAG = Player.class.getSimpleName();
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_ID_PREF = "playerId";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("users");

    private PlayerInfo playerInfo;
    private QRLibrary qrLibrary;
    private String playerId = null;
    private Context context;

    public Player(Context context) {
        this.context = context;
        playerInfo = new PlayerInfo();
        qrLibrary = new QRLibrary();
    }

    public String getPlayerId() {
        // fetched lazily, but only once
        if (playerId == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            playerId = sharedPreferences.getString(PLAYER_ID_PREF, "");
            // generated lazily, only once
            if (playerId.isEmpty()) {
                setPlayerId(UUID.randomUUID().toString());
                this.getPlayerInfo().generateUsername();
            }
        }
        Log.d(TAG, String.format("retrieved uuid: %s", playerId));
        return playerId;
    }

    public void setPlayerId(String id) {
        // to think about: when the id is changed, it's essentially a new player?
        playerId = id;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_ID_PREF, playerId);
        editor.apply();
        Log.d(TAG, String.format("set uuid: %s", playerId));
        savePlayer();
    }

    public void savePlayer() {
        HashMap<String, Object> playerData = new HashMap<>();
        if (playerId.length() > 0) {
            // If there’s some data in the EditText field, then we create a new key-value pair.
            playerData.put("Player Info", this.playerInfo);
            // The set method sets a unique id for the document
            collectionReference
                    .document(playerId)
                    .set(playerData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // These are a method which gets executed when the task is succeeded

                            Log.d(TAG, "Data has been added successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // These are a method which gets executed if there’s any problem
                            Log.d(TAG, "Data could not be added!" + e.toString());
                        }
                    });
        }
    }

    public void deletePlayer(PlayerInfo playerToDelete, Player admin) {
        PlayerInfo adminInfo = admin.getPlayerInfo();
        if (adminInfo.isAdmin()) {
            // Delete Player from database
            adminInfo.deletePlayerInfo(playerToDelete);
        }
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
