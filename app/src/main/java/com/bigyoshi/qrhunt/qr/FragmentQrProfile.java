package com.bigyoshi.qrhunt.qr;

import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.R;
import com.squareup.picasso.Picasso;

/**
 * Definition: Fragment used when the player wishes to delete their QR code
 * Note: NA
 * Issues: NA
 */
public class FragmentQrProfile extends DialogFragment {

    private int pos;
    private PlayableQrCode removeQR;
    private Player playerInfo;

    /**
     * Constructor method
     *  @param i int
     * @param currentQR QR to remove
     * @param playerInfo
     */
    public FragmentQrProfile(int i, PlayableQrCode currentQR, Player playerInfo) {
        this.pos = pos;
        this.removeQR = currentQR;
        this.playerInfo = playerInfo;
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

        // Display score
        TextView showScore = view.findViewById(R.id.qr_profile_qr_score);
        showScore.setText(String.valueOf(removeQR.getScore())+" Points");

        // Display numScan
        TextView showNumScanned = view.findViewById(R.id.qr_profile_num_scanned);
        showNumScanned.setText("01"); // HARD CODED FOR NOW

        // Display location
        TextView showLatLong = view.findViewById(R.id.qr_profile_qr_location);
        QrLocation qrLocation = removeQR.getLocation();
        if (qrLocation != null) {
            String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        // attach photo
        ImageView showPic = view.findViewById(R.id.qr_profile_image_placeholder);
        if (removeQR.getImageUrl() != null) {
            Picasso.get().load(removeQR.getImageUrl()).into(showPic);
        }
        showPic.setCropToPadding(true);

        TextView userName = view.findViewById(R.id.qr_profile_player_username);
        userName.setText(playerInfo.getUsername());


        Button deleteButton = view.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(view1 -> {
            FragmentProfile parentFrag = ((FragmentProfile) this.getParentFragment());
            parentFrag.libraryRemoveQR(pos, removeQR);
            getFragmentManager().beginTransaction().remove(this).commit();
        });

        ImageButton backButton = view.findViewById(R.id.qr_profile_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(FragmentQrProfile.this).commit();
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
}
