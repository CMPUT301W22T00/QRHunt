package com.bigyoshi.qrhunt.qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;


/**
 * Definition: Generates a QR code with the account specifications to access the account on other devices
 * Note: NA
 * Issues: This is not implement yet
 */
public class UnplayableQrCode extends Fragment {

    private String profileID;
    private Boolean isLogin;
    /**
     * Constructor method
     *
     */

    public UnplayableQrCode(String profileID, Boolean isLogin){
        this.profileID = profileID;
        this.isLogin = isLogin;
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
