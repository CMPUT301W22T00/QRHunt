package com.bigyoshi.qrhunt.qr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.bigyoshi.qrhunt.player.SelfPlayer;


/**
 * Definition: Generates a QR code with the account specifications to access the account on other devices
 * Note: NA
 * Issues: This is not implement yet
 */
public class UnplayableQrCode{

    private String profileID;
    private Boolean isLogin;
    private Player player;
    private SelfPlayer transferPlayer;
    private Fragment frag;
    private FragmentProfileBinding binding;

    /**
     * Constructor method
     *
     */
    public UnplayableQrCode(String profileID, Boolean isLogin, Fragment frag){
        this.profileID = profileID;
        this.isLogin = isLogin;
        this.frag = frag;
        //if (this.isLogin) { getLogInInfo(); }

        player = Player.fromPlayerId(profileID);
        FragmentProfile profile = new FragmentProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentProfile.IS_OWN_PROFILE, ProfileType.VISITOR_VIEW);
        bundle.putSerializable("player", player);
        bundle.putSerializable("isActivity", 0);
        profile.setArguments(bundle);

        if (!this.isLogin) {
            getGameStatusInfo(profile);
        } else {
            getLogInInfo();
        }
    }

    /**
     * Gets game status features and displays it in a view
     *
     */
    public void getGameStatusInfo(FragmentProfile profile){
        // Function used to get game status features and displaying it in a view (UI)
        //if (!isAdded()) return;
        frag.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.scanner_view, profile, "profile")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Gets accessing account or "log-in" specifications
     *
     */
    public void getLogInInfo(){
        // Function used to get log-in specifications
        // We need to somehow, get this info -> log in immediately
        transferPlayer.setPlayerId(profileID);
    }
}
