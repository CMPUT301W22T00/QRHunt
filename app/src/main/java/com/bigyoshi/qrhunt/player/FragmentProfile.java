package com.bigyoshi.qrhunt.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentProfileBinding;
import com.bigyoshi.qrhunt.qr.FragmentQrProfile;
import com.bigyoshi.qrhunt.qr.PlayableQrCode;
import com.bigyoshi.qrhunt.qr.QrLibraryGridViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Definition: Fragment class for the player profile screen Note: NA Issues: Rankings are not
 * implemented / displayed, QR Code GameStatus is not implemented, No QRLibrary display
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
    private ArrayList<PlayableQrCode> qrCodesList;
    private ArrayAdapter<PlayableQrCode> qrCodesAdapter;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity()
                .getOnBackPressedDispatcher()
                .addCallback(
                        this,
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
     * @param inflater Inflater
     * @param container Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        playerInfo = (Player) getArguments().getSerializable("player");
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView rank = root.findViewById(R.id.player_profile_rank);
        rank.setText(String.format(Locale.CANADA, "%d", playerInfo.getRankInfo().getTotalScore()));
        TextView bestUnique = root.findViewById(R.id.player_profile_unique_score_text);
        bestUnique.setText(String.valueOf(playerInfo.getBestUniqueQr().getScore()));

        QRTotalValue = root.findViewById(R.id.player_profile_score);
        username = root.findViewById(R.id.player_profile_username_title);
        contactsButton = root.findViewById(R.id.player_profile_contact_button);
        totalScanned = root.findViewById(R.id.player_profile_scanned_text);
        View calloutView = View.inflate(getContext(), R.layout.player_contact_callout, container);
        TextView combined = calloutView.findViewById(R.id.contact_call_out_text);

        showAll = root.findViewById(R.id.player_profile_grid_view);
        qrCodesList = playerInfo.qrLibrary.getQrCodes();
        qrCodesAdapter = new QrLibraryGridViewAdapter(root.getContext(), qrCodesList);
        showAll.setAdapter(qrCodesAdapter);
        showAll.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    new FragmentQrProfile(i, qrCodesList.get(i), playerInfo)
                            .show(getChildFragmentManager(), "LIBRARY_REMOVE_QR");
                    onPause();
                });


        ImageButton sortButton = root.findViewById(R.id.player_profile_sort_button);
        sortButton.setOnClickListener(view -> {
                if (playerInfo.qrLibrary.getScoredSorted() < 0) {
                    qrCodesList = playerInfo.qrLibrary.sortScoreAscending();
                } else {
                    qrCodesList = playerInfo.qrLibrary.sortScoreDescending();
                }
                qrCodesAdapter.notifyDataSetChanged();
        });


        settingButton = root.findViewById(R.id.player_profile_settings_button);

        QRTotalValue.setText(Integer.toString(playerInfo.getTotalScore()));
        username.setText(playerInfo.getUsername());
        totalScanned.setText(Integer.toString(qrCodesList.size()));

        settingButton.setOnClickListener(
                v -> {
                    FragmentPlayerSetting profileSetting = new FragmentPlayerSetting();
                    FragmentTransaction fragmentTransaction =
                            getChildFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("player", playerInfo);
                    profileSetting.setArguments(bundle);
                    fragmentTransaction.add(R.id.player_profile, profileSetting, "setting");
                    fragmentTransaction.commit();
                });

        /*
           https://www.youtube.com/watch?v=IxHfWg-M0bI
           https://github.com/douglasjunior/android-simple-tooltip
           https://github.com/douglasjunior/android-simple-tooltip/issues/24
        */
        contactsButton.setOnClickListener(
                view -> {
                    String email = playerInfo.getContact().getEmail();
                    String social = playerInfo.getContact().getSocial();
                    if (!email.matches("") || !social.matches("")) {
                        String together = email + "\n" + social;
                        combined.setText(together);
                        new SimpleTooltip.Builder(getContext())
                                .anchorView(contactsButton)
                                .gravity(Gravity.BOTTOM)
                                .text(together)
                                .arrowColor(
                                        getResources().getColor(R.color.accent_grey_blue_dark))
                                .textColor(getResources().getColor(R.color.text_off_white))
                                .animated(true)
                                .transparentOverlay(true)
                                .backgroundColor(
                                        getResources().getColor(R.color.accent_grey_blue_dark))
                                .contentView(R.layout.player_contact_callout, combined.getId())
                                .build()
                                .show();
                    }
                });

        return root;
    }

    /**
     * Deletes a QR form the QR Library
     *
     * @param pos position in library
     */
    public void libraryRemoveQR(int pos, PlayableQrCode removeQR) {
        qrCodesList.remove(pos);
        qrCodesAdapter.notifyDataSetChanged();
        db = FirebaseFirestore.getInstance();
        removeQR.deleteFromDb(db, playerInfo.getPlayerId());
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodesAdapter.notifyDataSetChanged();
        showAll.invalidate();
    }
}
