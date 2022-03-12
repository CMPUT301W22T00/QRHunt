package com.bigyoshi.qrhunt;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;
import com.google.android.material.textview.MaterialTextView;

import org.osmdroid.config.Configuration;

public class Profile extends Fragment {
    private FragmentProfileBinding binding;
    private MaterialTextView QRTotalValue;
    private MaterialTextView username;
    private MaterialTextView totalRank;
    private MaterialTextView totalScanned;
    private MaterialTextView uniqueRank;
    private PlayerInfo playerInfo;

    public Profile(Player player){
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

        QRTotalValue.setText(Integer.toString(playerInfo.getQRTotal()));
        username.setText(playerInfo.getUsername());
        totalRank.setText(Integer.toString(playerInfo.getQRTotalRank()));
        totalScanned.setText(Integer.toString(playerInfo.getQRTotalScanned()));
        uniqueRank.setText(Integer.toString(playerInfo.getHighestValueQRRank()));

        return root;
    }
}
