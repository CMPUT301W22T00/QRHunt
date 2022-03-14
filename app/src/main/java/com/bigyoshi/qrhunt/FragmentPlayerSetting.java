package com.bigyoshi.qrhunt;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsBinding;

import org.osmdroid.config.Configuration;

/**
 * Definition: Settings menu for editing user's profile and generating QR to access account on other devices
 *
 *
 */
public class FragmentPlayerSetting extends Fragment {
    private FragmentUserSettingsBinding binding;
    private Player playerInfo;
    private TextView playerProfileSettings;
    private ImageView backButton;

    /**
     * Constructor
     * @param playerInfo
     */
    public FragmentPlayerSetting(Player playerInfo){
        this.playerInfo = playerInfo;
    }

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        playerProfileSettings = root.findViewById(R.id.settings_edit_profile);
        backButton = root.findViewById(R.id.settings_back_arrow);

        playerProfileSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentPlayerProfileSetting profileSetting = new FragmentPlayerProfileSetting(playerInfo);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.playerSettings, profileSetting, "setting");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putSerializable("info", playerInfo);
                getParentFragmentManager().setFragmentResult("getInfo", result);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        getActivity().getSupportFragmentManager().setFragmentResultListener("getNewInfo", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Player newInfo = (Player) result.getSerializable("newInfo");
                playerInfo.setUsername(newInfo.getUsername());
                playerInfo.setContact(newInfo.getContact());
            }
        });

        return root;
    }
}
