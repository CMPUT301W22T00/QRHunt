package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;

import org.osmdroid.config.Configuration;

/**
 * Definition:
 *
 *
 */
public class FragmentProfile extends Fragment {
    private FragmentProfileBinding binding;
    private TextView QRTotalValue;
    private TextView username;
    private TextView totalRank;
    private TextView totalScanned;
    private TextView uniqueRank;
    private Player playerInfo;
    private ImageButton settingButton;
    private int lastDestination;
    private Player player;

    /**
     *
     * @param player
     * @param lastDestination
     */
    public FragmentProfile(Player player, int lastDestination){
         this.playerInfo = player;
         this.player = player;
         this.lastDestination = lastDestination;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getContext(), MainActivity.class);
                Bundle prevNav = new Bundle();
                prevNav.putSerializable("previous", lastDestination);
                intent.putExtras(prevNav);
                startActivity(intent);
            }
        });
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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

        QRTotalValue.setText(Integer.toString(playerInfo.getTotalScore()));
        username.setText(playerInfo.getUsername());
        totalRank.setText("0"); // NEED TO UPDATE
        totalScanned.setText(Integer.toString(0)); // NEED TO UPDATE
        uniqueRank.setText(Integer.toString(0)); // NEED TO UPDATE

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

        getActivity().getSupportFragmentManager().setFragmentResultListener("getInfo", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Player newInfo = (Player) result.getSerializable("info");
                playerInfo.setUsername(newInfo.getUsername());
                playerInfo.setContact(newInfo.getContact());
                username.setText(playerInfo.getUsername());
                playerInfo.updateDB();
            }
        });

        return root;
    }


}
