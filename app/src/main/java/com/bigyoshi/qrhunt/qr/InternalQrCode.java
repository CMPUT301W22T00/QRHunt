package com.bigyoshi.qrhunt.qr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.bigyoshi.qrhunt.player.SelfPlayer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Definition: Generates a QR code with the account specifications to access the account on other devices
 * Note: NA
 * Issues: This is not implement yet
 */
public class InternalQrCode {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_ID_PREF = "playerId";
    private static final String TAG = InternalQrCode.class.getSimpleName();
    private String playerId;
    private Boolean isLogin;
    private Player player;
    private SelfPlayer transferPlayer;
    private Fragment frag;
    private FragmentProfileBinding binding;

    /**
     * Constructor method
     */
    public InternalQrCode(String playerId, Boolean isLogin, Fragment frag) {
        this.playerId = playerId;
        this.isLogin = isLogin;
        this.frag = frag;

        if (!this.isLogin) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = db.collection("users");
            collectionReference.document(playerId).get().addOnCompleteListener(querySnapshotTask -> {
                FragmentProfile profile = new FragmentProfile();
                Bundle bundle = new Bundle();
                bundle.putSerializable(FragmentProfile.IS_OWN_PROFILE, ProfileType.VISITOR_VIEW);
                bundle.putSerializable("player", Player.fromDoc(querySnapshotTask.getResult()));
                bundle.putSerializable("isActivity", 1);
                profile.setArguments(bundle);
                showSharedProfile(profile);
            });
        } else {
            setTransferAccount();
        }
    }

    /**
     * Gets game status features and displays it in a view
     */
    public void showSharedProfile(FragmentProfile profile) {
        // Function used to get game status features and displaying it in a view (UI)
        //if (!isAdded()) return;
        frag.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, profile, "profile")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Gets accessing account or "log-in" specifications
     */
    public void setTransferAccount() {
        SharedPreferences sharedPreferences = frag.getContext().getSharedPreferences(SHARED_PREFS,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PLAYER_ID_PREF, playerId).apply();
        Log.d(TAG, String.format("set uuid for transfer: %s", this.playerId));
        frag.getActivity().recreate();
    }
}