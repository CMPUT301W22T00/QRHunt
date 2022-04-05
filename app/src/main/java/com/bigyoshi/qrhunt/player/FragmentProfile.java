package com.bigyoshi.qrhunt.player;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
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
import java.util.Locale;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Definition: Fragment class for the player profile screen
 * Note: N/A
 * Issues: N/A
 */
public class FragmentProfile extends Fragment {
    public static final String PROFILE_TYPE_KEY = "isOwnProfile";
    private FragmentProfileBinding binding;
    private TextView QRTotalValue;
    private TextView username;
    private TextView totalScanned;
    private Player playerInfo;
    private Player selfPlayer;
    private ImageButton settingButton;
    private ImageButton contactsButton;
    private int lastDestination;
    private GridView qrGridView;
    private ArrayList<PlayableQrCode> qrCodesList;
    private ArrayAdapter<PlayableQrCode> qrCodesAdapter;
    private FirebaseFirestore db;
    private ProfileType viewType;
    public static final String TAG = FragmentProfile.class.getSimpleName();


    /**
     * todo does smth
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity()
                .getOnBackPressedDispatcher()
                .addCallback(this,
                            new OnBackPressedCallback(true) {

                                @Override
                                public void handleOnBackPressed() {
                                    if (lastDestination == 1) {
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        Bundle prevNav = new Bundle();
                                        prevNav.putSerializable("previous", lastDestination);
                                        intent.putExtras(prevNav);
                                        startActivity(intent);
                                    } else {
                                        getFragmentManager().popBackStackImmediate();
                                    }
                                }
                });
    }

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           inflater
     * @param container          where the fragment is contained
     * @param savedInstanceState savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        selfPlayer = (Player) getArguments().getSerializable("selfPlayer");
        playerInfo = (Player) getArguments().getSerializable("player");
        lastDestination = (Integer) getArguments().getSerializable("isActivity");
        viewType = (ProfileType) getArguments().getSerializable(PROFILE_TYPE_KEY);
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

        qrGridView = root.findViewById(R.id.player_profile_grid_view);
        qrCodesList = playerInfo.qrLibrary.getQrCodes();
        if (qrCodesList.size() == 0){
            root.findViewById(R.id.qr_library_no_results_text).setVisibility(View.VISIBLE);
        } else {
            root.findViewById(R.id.qr_library_no_results_text).setVisibility(View.INVISIBLE);
        }
        qrCodesAdapter = new QrLibraryGridViewAdapter(root.getContext(), qrCodesList);
        qrGridView.setAdapter(qrCodesAdapter);
        //qrGridView.setNestedScrollingEnabled(true); // Commented out to test
        setGridViewHeight(qrGridView);
        qrCodesAdapter.notifyDataSetChanged();
        qrGridView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    new FragmentQrProfile(i, qrCodesList.get(i), playerInfo, viewType, selfPlayer)
                            .show(getChildFragmentManager(), "LIBRARY_REMOVE_QR");
                    onPause();
                });


        ImageButton sortButton = root.findViewById(R.id.player_profile_sort_button);
        TextView sortIndication = root.findViewById(R.id.sort_direction);
        sortButton.setOnClickListener(view -> {
                if (playerInfo.qrLibrary.getScoredSorted() < 0) {
                    qrCodesList = playerInfo.qrLibrary.sortScoreDescending();
                    setGridViewHeight(qrGridView);
                    sortIndication.setText("Score Descending");
                    sortButton.setScaleY(1);
                } else {
                    qrCodesList = playerInfo.qrLibrary.sortScoreAscending();
                    setGridViewHeight(qrGridView);
                    sortIndication.setText("Score Ascending");
                    sortButton.setScaleY(-1);
                }
                qrCodesAdapter.notifyDataSetChanged();
        });


        settingButton = root.findViewById(R.id.player_profile_settings_button);
        if (viewType == ProfileType.VISITOR_VIEW) {
            settingButton.setVisibility(View.INVISIBLE);
        }
        else {
            settingButton.setVisibility(View.VISIBLE);
        }

        QRTotalValue.setText(Integer.toString(playerInfo.getTotalScore()));
        username.setText(playerInfo.getUsername());
        totalScanned.setText(Integer.toString(qrCodesList.size()));

        if (viewType == ProfileType.OWN_VIEW) {
            settingButton.setOnClickListener(
                    v -> {
                        FragmentPlayerSetting profileSetting = new FragmentPlayerSetting();
                        FragmentTransaction fragmentTransaction =
                                getChildFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("player", playerInfo);
                        bundle.putSerializable("isActivity", lastDestination);
                        profileSetting.setArguments(bundle);
                        fragmentTransaction.add(R.id.player_profile, profileSetting, "setting");
                        fragmentTransaction.commit();
                    });
        }
        else if (viewType == ProfileType.ADMIN_VIEW) {
            settingButton.setOnClickListener(
                    v -> {
                        SimpleTooltip deleteAccountCallout = new SimpleTooltip.Builder(getContext())
                                .anchorView(settingButton)
                                .gravity(Gravity.CENTER)
                                .showArrow(false)
                                .animated(false)
                                .transparentOverlay(true)
                                .contentView(R.layout.admin_delete_callout)
                                .dismissOnOutsideTouch(true)
                                .dismissOnInsideTouch(false)
                                .build();

                        Log.d(TAG, "onCreateView: player id is " + playerInfo.getPlayerId());

                        deleteAccountCallout.findViewById(R.id.delete_call_out_button)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder deleteConfirmationPromptBuilder
                                                = new AlertDialog.Builder(getContext());

                                        deleteConfirmationPromptBuilder.setView(
                                                R.layout.admin_delete_profile_dialog);

                                        AlertDialog confirmDeletePrompt
                                                = deleteConfirmationPromptBuilder.create();

                                        confirmDeletePrompt.show();

                                        confirmDeletePrompt.findViewById(
                                                R.id.delete_qr_dialog_confirm_button)
                                                .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db = FirebaseFirestore.getInstance();
                                                    db.collection("users")
                                                            .document(playerInfo.getPlayerId())
                                                            .delete()
                                                            .addOnSuccessListener(unused -> {
                                                                db.collection("user")
                                                                        .document(playerInfo.getPlayerId())
                                                                        .delete();
                                                                Log.d(TAG, "Successfully removed player from data base");
                                                            })
                                                            .addOnFailureListener(e -> Log.w(TAG, "Error removing player from data base", e));


                                                    AlertDialog.Builder accountDeletedConfirmationBuilder =
                                                            new AlertDialog.Builder(getContext());

                                                    accountDeletedConfirmationBuilder.setView(R.layout.admin_delete_profile_confirmation);

                                                    AlertDialog accountDeletedConfirmation =
                                                        accountDeletedConfirmationBuilder.create();

                                                    accountDeletedConfirmation.show();

                                                    accountDeletedConfirmation.findViewById(
                                                            R.id.delete_confirm_confirm_button)
                                                            .setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            accountDeletedConfirmation.dismiss();
                                                        }
                                                    });

                                                    confirmDeletePrompt.dismiss();

                                                }
                                            });

                                        confirmDeletePrompt.findViewById(
                                                R.id.delete_qr_dialog_cancel_button)
                                                .setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        confirmDeletePrompt.dismiss();
                                                    }
                                                });
                                    }
                                });

                        deleteAccountCallout.show();



                    });
        }

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
                                .contentView(R.layout.player_contact_callout, R.id.delete_qr_callout_button)
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

    /**
     * todo does smth
     */
    @Override
    public void onResume() {
        super.onResume();
        qrCodesAdapter.notifyDataSetChanged();
        qrGridView.invalidate();
    }

    /**
     * todo does smth
     *
     * @param gridview todo tag
     */
    public void setGridViewHeight(GridView gridview) {
        //Get the adapter of gridview
        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalNum = listAdapter.getCount();
        int totalHeight = 0;
        //Calculate the sum of the height of each column
        int numRows = Math.round(totalNum/3) + 1;
        if (!listAdapter.isEmpty()) {
            View listItem = listAdapter.getView(0, null, gridview);
            listItem.measure(0, 0);
            totalHeight += (listItem.getMeasuredWidth() * 5 + listItem.getMeasuredWidth()) * numRows;
        }

        //Get the layout parameters of the gridview
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        //set height
        params.height = totalHeight;
        //Setting parameters
        gridview.setLayoutParams(params);
    }
}
