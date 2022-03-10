package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.UUID;

public class Player {
    private static final String TAG = Player.class.getSimpleName();
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_ID_PREF = "playerId";

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
