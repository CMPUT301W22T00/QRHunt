package com.bigyoshi.qrhunt.player;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.bigyoshi.qrhunt.R;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/**
 * Definition: Class specifically for the player themselves
 * Note: N/A
 * Issues: N/A
 */
public class SelfPlayer extends Player implements Serializable {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_ID_PREF = "playerId";
    private static final String TAG = SelfPlayer.class.getSimpleName();

    /**
     * Constructor method
     * @param context   context
     */
    public SelfPlayer(Context context) {
        super(null, context);
    }

    /**
     * Gets the player id and checks if they already have an account through shared prefs
     *
     * @return playerId
     */
    public String getPlayerId() {
        // fetched lazily, but only once
        if (playerId == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            playerId = sharedPreferences.getString(PLAYER_ID_PREF, "");
            // generated lazily, only once
            if (playerId.isEmpty()) {
                username = generateUsername();
                setPlayerId(UUID.randomUUID().toString());
                savePlayer();
            }
            Log.d(TAG, String.format("retrieved uuid: %s", playerId));
        }
        return playerId;
    }

    /**
     * Setter method
     *
     * @param playerId  player id
     */
    public void setPlayerId(String playerId) {
        // to think about: when the id is changed, it's essentially a new player?
        this.playerId = playerId;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_ID_PREF, this.playerId);
        editor.apply();
        Log.d(TAG, String.format("set uuid: %s", this.playerId));
    }

    /**
     * Generates random unique username when account is created
     *
     * @return String representing generatedUsername
     */
    public String generateUsername() {
        // Random unique username generated when account is first created
        Random rand = new Random();
        Resources res = context.getResources();
        String[] adj = res.getStringArray(R.array.adjectives);
        String[] noun = res.getStringArray(R.array.noun);
        String adjName = adj[rand.nextInt(adj.length - 1)];
        String nounName = noun[rand.nextInt(noun.length - 1)];
        int upperbound = 100;
        String numName = Integer.toString(rand.nextInt(upperbound));
        String generatedName = adjName + nounName + numName;
        if (generatedName.length() > 12){
            generatedName = generatedName.substring(0,12);
        }
        return  generatedName;
    }
}
