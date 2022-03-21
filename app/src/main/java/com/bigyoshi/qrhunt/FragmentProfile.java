package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Definition: Fragment class for the player profile screen
 * Note: NA
 * Issues: Rankings are not implemented / displayed, QR Code GameStatus is not implemented, No QRLibrary display
 */
public class FragmentProfile extends Fragment {
    private FragmentProfileBinding binding;
    private TextView QRTotalValue;
    private TextView username;
    private TextView totalScanned;
    private Player playerInfo;
    private ImageButton settingButton;
    private ImageButton contactsButton;
    private int lastDestination;
    private GridView showAll;
    private HashMap<String, PlayableQRCode> qrCodes;
    private ArrayList<PlayableQRCode> qrCodesList;
    private ArrayAdapter<PlayableQRCode> qrCodesAdapter;
    private FirebaseFirestore db;


    /**
     * Constructor method
     *
     * @param player          Current player
     * @param lastDestination Previous navigation destination (by bottom navigation)
     */
    public FragmentProfile(Player player, int lastDestination){

         this.playerInfo = player;
         this.lastDestination = lastDestination;
    }

    /**
     * Creates instance of fragment, and handles where the activity goes after pressing back button
     * (eg, either to scanner or map)
     *
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {

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
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        QRTotalValue = root.findViewById(R.id.profile_score_text);
        username = root.findViewById(R.id.profile_username_title);
        contactsButton = root.findViewById(R.id.profile_information_button);
        totalScanned = root.findViewById(R.id.profile_codes_scanned);

        showAll = root.findViewById(R.id.profile_QR_grid);
        qrCodes = new HashMap<>();
        qrCodes = playerInfo.qrLibrary.getQrCodes();
        Collection<PlayableQRCode> temp = qrCodes.values();
        qrCodesList = new ArrayList<>(temp);
        qrCodesAdapter = new QrLibraryGridViewAdapter(root.getContext(), qrCodesList);
        showAll.setAdapter(qrCodesAdapter);
        showAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new FragmentLibraryRemoveQR(i, qrCodesList.get(i)).show(getChildFragmentManager(), "LIBRARY_REMOVE_QR");
            }
        });


        settingButton = root.findViewById(R.id.profile_settings_button);

        QRTotalValue.setText(Integer.toString(playerInfo.getTotalScore()));
        username.setText(playerInfo.getUsername());
        totalScanned.setText(Integer.toString(qrCodesList.size()));

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

        /*
            https://www.youtube.com/watch?v=IxHfWg-M0bI
            https://github.com/douglasjunior/android-simple-tooltip
         */
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = playerInfo.getContact().getEmail();
                String social = playerInfo.getContact().getSocial();
                if (!email.matches("") || !social.matches("")){
                    String together = email + "\n" + social;
                    View calloutView = inflater.inflate(R.layout.player_contact_callout,container,false);
                    TextView calloutText = calloutView.findViewById(R.id.callout_text);
                    calloutText.setText("AAAAAAAAAAAAAAAAAAAAAAAAAA");
                    new SimpleTooltip.Builder(getContext())
                            .anchorView(contactsButton)
                            .gravity(Gravity.BOTTOM)
                            .text(together)
                            .arrowColor(getResources().getColor(R.color.accent_grey_blue_dark))
                            .textColor(getResources().getColor(R.color.text_off_white))
                            .animated(true)
                            .transparentOverlay(true)
                            .backgroundColor(getResources().getColor(R.color.accent_grey_blue_dark))
                            .highlightShape(OverlayView.HIGHLIGHT_SHAPE_OVAL)
                            .build()
                            .show();
                    }
            }
        });

        getActivity().getSupportFragmentManager().setFragmentResultListener("getInfo",
                this,
                new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Player newInfo = (Player) result.getSerializable("info");
                playerInfo.setUsername(newInfo.getUsername());
                playerInfo.getContact().setEmail(newInfo.getContact().getEmail());
                playerInfo.getContact().setSocial(newInfo.getContact().getSocial());
                username.setText(playerInfo.getUsername());
                playerInfo.updateDB();
            }
        });

        return root;
    }

    /**
     * Deletes a QR form the QR Library
     *
     * @param pos position in library
     */
    public void libraryRemoveQR(int pos, PlayableQRCode removeQR) {
        qrCodesList.remove(pos);
        qrCodesAdapter.notifyDataSetChanged();
        db = FirebaseFirestore.getInstance();
        removeQR.deleteFromDb(db, playerInfo.getPlayerId());
    }


}
