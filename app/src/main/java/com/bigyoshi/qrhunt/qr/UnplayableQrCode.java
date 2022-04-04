package com.bigyoshi.qrhunt.qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;


/**
 * Definition: Generates a QR code with the account specifications to access the account on other devices
 * Note: NA
 * Issues: This is not implement yet
 */
public class UnplayableQrCode extends Fragment {

    private String profileID;
    private Boolean isLogin;
    private Player player;
    /**
     * Constructor method
     *
     */

    public UnplayableQrCode(String profileID, Boolean isLogin){
        this.profileID = profileID;
        this.isLogin = isLogin;
        if (!this.isLogin) { getGameStatusInfo(); }


    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }
    /**
     * Gets game status features and displays it in a view
     *
     */
    public void getGameStatusInfo(){
        // Function used to get game status features and displaying it in a view (UI)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(this.profileID).get()
            .addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   DocumentSnapshot doc = task.getResult();
                   player = Player.fromDoc(doc);
               }
        });
        FragmentProfile profile = new FragmentProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentProfile.IS_OWN_PROFILE, ProfileType.VISITOR_VIEW);
        bundle.putSerializable("player", player);
        bundle.putSerializable("isActivity", 0);
        profile.setArguments(bundle);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_bar, profile, "profile")
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
    }
}
