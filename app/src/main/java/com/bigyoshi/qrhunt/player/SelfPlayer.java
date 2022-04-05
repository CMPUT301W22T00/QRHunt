package com.bigyoshi.qrhunt.player;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.bigyoshi.qrhunt.R;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

// class specifically for the player themselves
public class SelfPlayer extends Player implements Serializable {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_ID_PREF = "playerId";
    private static final String TAG = SelfPlayer.class.getSimpleName();

    public SelfPlayer(Context context) {

        super(context);

    }

    public String getPlayerId() {
        // fetched lazily, but only once
        if (playerId == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            playerId = sharedPreferences.getString(PLAYER_ID_PREF, "");
            // generated lazily, only once
            if (playerId.isEmpty()) {
                setPlayerId(UUID.randomUUID().toString());
            }
            Log.d(TAG, String.format("retrieved uuid: %s", playerId));
        }
        return playerId;
    }

    public void setPlayerId(String playerId) {
        // to think about: when the id is changed, it's essentially a new player?
        this.playerId = playerId;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_ID_PREF, this.playerId);
        editor.apply();
        Log.d(TAG, String.format("set uuid: %s", this.playerId));
        savePlayer();
    }
}
