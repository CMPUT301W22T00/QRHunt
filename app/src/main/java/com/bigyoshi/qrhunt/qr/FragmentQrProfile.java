package com.bigyoshi.qrhunt.qr;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import com.bigyoshi.qrhunt.player.ProfileType;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Definition: Fragment used when the player wishes to delete their QR code
 * Note: NA
 * Issues: NA
 */
public class FragmentQrProfile extends DialogFragment {

    private int pos;
    private PlayableQrCode qr;
    private Player player;
    private ProfileType profileType;
    private FirebaseFirestore db;
    private Player selfPlayer;

    /**
     * Constructor method
     * @param pos int
     * @param qr QR to remove
     * @param player    player that the account belongs to
     * @param selfPlayer user's own account
     */
    public FragmentQrProfile(int pos, PlayableQrCode qr, Player player, ProfileType profileType, Player selfPlayer) {
        this.pos = pos;
        this.qr = qr;
        this.player = player;
        this.profileType = profileType;
        this.selfPlayer = selfPlayer;
        ;
    }

    /**
     * Creates the view for deleting a QR code
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_player_profile, container, false);

        // Get Data Base
        db = FirebaseFirestore.getInstance();

        // Display score
        TextView showScore = view.findViewById(R.id.qr_profile_qr_score);
        showScore.setText(String.valueOf(qr.getScore())+" Points");

        // Display numScan and unique flag
        TextView showNumScanned = view.findViewById(R.id.qr_profile_num_scanned);
        ImageView uniqueFlag = view.findViewById(R.id.qr_profile_unique_flag);
        showNumScanned.setText("Scans: "+ qr.getNumScanned());
        if (qr.getNumScanned().matches("1")) { uniqueFlag.setVisibility(View.VISIBLE); }

        // Display scanned Flag
        if (!selfPlayer.getPlayerId().matches(player.getPlayerId())) {
            if (selfPlayer.getPlayerId().matches(qr.getPlayerId())) {
                ImageView scannedFlag = view.findViewById(R.id.qr_profile_scanned_flag);
                scannedFlag.setVisibility(View.VISIBLE);
            }
        }

        // Display location
        TextView showLatLong = view.findViewById(R.id.qr_profile_qr_location);
        QrLocation qrLocation = qr.getLocation();
        if (qrLocation != null) {
            String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("No Location");
        }

        // Attach Image
        ImageView showPic = view.findViewById(R.id.qr_profile_image_placeholder);
        if (qr.getImageUrl() != null) {
            Picasso.get().load(qr.getImageUrl()).into(showPic);
        }
        showPic.setCropToPadding(true);

        // Display Username
        TextView userName = view.findViewById(R.id.qr_profile_player_username);
        userName.setText(player.getUsername());


        ImageButton deleteButton = view.findViewById(R.id.qr_profile_option_menu);
        if (profileType == ProfileType.ADMIN_VIEW || profileType == ProfileType.OWN_VIEW || player.getPlayerId().equals(qr.getPlayerId())) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setOnClickListener(view1 -> {

            SimpleTooltip deleteQRConfirmation = new SimpleTooltip.Builder(getContext())
                    .anchorView(deleteButton)
                    .gravity(Gravity.CENTER)
                    .showArrow(false)
                    .textColor(ContextCompat.getColor(getContext(), R.color.main_off_white_blue_light))
                    .animated(false)
                    .transparentOverlay(true)
                    .backgroundColor(ContextCompat.getColor(getContext(), R.color.accent_grey_blue_dark))
                    .contentView(R.layout.delete_qr_callout)
                    .text(getString(R.string.delete))
                    .dismissOnInsideTouch(false)
                    .dismissOnOutsideTouch(true)
                    .build();


            deleteQRConfirmation.findViewById(R.id.delete_qr_callout_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder deleteQRConfirmationBuilder = new AlertDialog.Builder(getContext())
                            .setView(R.layout.delete_qr_confirm_dialog);

                    AlertDialog deleteQRConfirmation = deleteQRConfirmationBuilder.create();

                    deleteQRConfirmation.show();

                    deleteQRConfirmation.findViewById(R.id.delete_qr_dialog_cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteQRConfirmation.dismiss();
                        }
                    });

                    deleteQRConfirmation.findViewById(R.id.delete_qr_dialog_confirm_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeQR();
                            deleteQRConfirmation.dismiss();
                        }
                    });
                }
            });

            deleteQRConfirmation.show();

        });

        // Back Button
        ImageButton backButton = view.findViewById(R.id.qr_profile_back_button);
        backButton.setOnClickListener(view2 -> {
            getFragmentManager().beginTransaction().remove(FragmentQrProfile.this).commit();
        });

        // Display Comments

        ListView commentList = view.findViewById(R.id.qr_profile_comment_list);
        ArrayList<QRComment> comments = new ArrayList();
        QRCommentAdapter commentAdapter = new QRCommentAdapter(view.getContext(), comments);
        commentList.setAdapter(commentAdapter);
        commentList.setNestedScrollingEnabled(true); // Commented out to test new solution
        db.collection("users").document(player.getPlayerId()).collection("qrCodes").document(qr.getId()).collection("comments")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        QRComment comment = new QRComment(doc.getData().get("comment").toString(), doc.getData().get("username").toString());
                        comments.add(comment);
                        setListViewHeight(commentList);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });



        // Add QRComment
        ImageButton commentButton = view.findViewById(R.id.qr_profile_send_comment_button);
        commentButton.setOnClickListener(view3 -> {
            EditText newCommentText = view.findViewById(R.id.qr_profile_add_comment);
            HashMap<String, String> map = new HashMap<>();

            if (!newCommentText.getText().toString().isEmpty()) {
                map.put("comment", newCommentText.getText().toString());
                map.put("username", selfPlayer.getUsername());

                QRComment newComment = new QRComment(
                        newCommentText.getText().toString(), selfPlayer.getUsername());
                comments.add(newComment);
                db.collection("users").document(player.getPlayerId()).collection("qrCodes").document(qr.getId())
                        .collection("comments")
                        .document(newCommentText.getText().toString()).set(map);
                newCommentText.getText().clear();
                setListViewHeight(commentList);
                commentAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    // Me being stupid and using a DialogFragment as the base early on
    // Makes it full screen rather than windowed view
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //Dynamically set the height for the listview (display as many items as there are)
    public void setListViewHeight(ListView listView) {
        //Get the adapter of listView
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 60;
        //listAdapter.getCount() returns the number of data items
        for (int i = 0,len = listAdapter.getCount(); i <len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //listView.getDividerHeight() gets the height occupied by the divider between sub items
        //params.height finally gets the height required for complete display of the entire ListView
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
               (listAdapter .getCount()-1) + 50);
        if (params.height < 200) { params.height = 200; }
        listView.setLayoutParams(params);
    }

    public void removeQR(){
        FragmentProfile parentFrag = ((FragmentProfile) this.getParentFragment());
        parentFrag.libraryRemoveQR(pos, qr);
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
