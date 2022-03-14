package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import com.google.common.collect.ArrayListMultimap;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Definition: Fragment class for the player profile screen
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
    private GridView showAll;
    private HashMap<String, PlayableQRCode> qrCodes;
    private ArrayList<PlayableQRCode> qrCodesList;
    private ArrayAdapter<PlayableQRCode> qrCodesAdapter;
    private QrLibraryGridViewAdapter qrListAdapter;



    /**
     * constructor
     * @param player
     * @param lastDestination
     */
    public FragmentProfile(Player player, int lastDestination){
         this.playerInfo = player;
         this.player = player;
         this.lastDestination = lastDestination;
    }

    /**
     * creates instance of fragment, and handles where the activity goes after pressing back button
     * (eg, either to scanner or map)
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
     * sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
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
        showAll = root.findViewById(R.id.profile_QR_grid);
        qrCodes = player.qrLibrary.getQrCodes();
        Collection<PlayableQRCode> temp = qrCodes.values();
        qrCodesList = new ArrayList<>(temp);
        qrCodesAdapter = new QrLibraryGridViewAdapter(getContext(), qrCodesList);
        showAll.setAdapter(qrCodesAdapter);



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
