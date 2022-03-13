package com.bigyoshi.qrhunt;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;
import com.google.android.material.textview.MaterialTextView;

import org.osmdroid.config.Configuration;

public class FragmentProfile extends Fragment {
    private FragmentProfileBinding binding;
    private MaterialTextView QRTotalValue;
    private MaterialTextView username;
    private MaterialTextView totalRank;
    private MaterialTextView totalScanned;
    private MaterialTextView uniqueRank;
    private PlayerInfo playerInfo;
    private ImageButton settingButton;

    public FragmentProfile(Player player){
         this.playerInfo = player.getPlayerInfo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        QRTotalValue = root.findViewById(R.id.profile_score_text);
        username = root.findViewById(R.id.profile_username_title);
        totalRank = root.findViewById(R.id.profile_rank_text);
        totalScanned = root.findViewById(R.id.profile_codes_scanned);
        uniqueRank = root.findViewById(R.id.profile_highest_unique);

        settingButton = root.findViewById(R.id.profile_settings_button);

        QRTotalValue.setText(Integer.toString(playerInfo.getQRTotal()));
        username.setText(playerInfo.getUsername());
        totalRank.setText(Integer.toString(playerInfo.getQRTotalRank()));
        totalScanned.setText(Integer.toString(playerInfo.getQRTotalScanned()));
        uniqueRank.setText(Integer.toString(playerInfo.getHighestValueQRRank()));

        settingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentPlayerSetting profileSetting = new FragmentPlayerSetting(playerInfo);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.playerProfile, profileSetting, "setting");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return root;
    }
}
