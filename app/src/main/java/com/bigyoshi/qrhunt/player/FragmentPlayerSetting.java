package com.bigyoshi.qrhunt.player;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsBinding;

import net.glxn.qrgen.android.QRCode;
/**
 * Definition: Settings menu for editing user's profile and generating QR to access account on other devices
 * Note: NA
 * Issues:
 */
public class FragmentPlayerSetting extends Fragment {
    private FragmentUserSettingsBinding binding;
    private Player playerInfo;
    private TextView playerEditProfile;
    private ImageView backButton;
    private TextView registerNewDevice;
    private TextView shareProfile;
    private int lastDestination;

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        playerInfo = (Player) getArguments().getSerializable("player");
        lastDestination = (Integer) getArguments().getSerializable("isActivity");
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        playerEditProfile = root.findViewById(R.id.player_settings_edit_profile_clickable);
        backButton = root.findViewById(R.id.player_settings_back_button);
        registerNewDevice = root.findViewById(R.id.player_settings_register_device_clickable);
        shareProfile = root.findViewById(R.id.player_settings_share_profile_clickable);

        playerEditProfile.setOnClickListener(v -> {
            DialogFragment dialogFragment = new FragmentPlayerProfileSetting();
            Bundle bundle = new Bundle();
            bundle.putSerializable("player", playerInfo);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getChildFragmentManager(), null);
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentProfile profile = new FragmentProfile();
                Bundle bundle = new Bundle();
                bundle.putSerializable("player", playerInfo);
                bundle.putSerializable("isActivity", lastDestination);
                profile.setArguments(bundle);
                bundle.putSerializable(FragmentProfile.IS_OWN_PROFILE, ProfileType.OWN_VIEW);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.player_settings, profile, "profile");
                fragmentTransaction.commit();
            }
        });

        registerNewDevice.setOnClickListener(v -> {
            String txtTransfer = "Scan to access your account on another device!";
            String registerCode = "qrhunt:transfer:" + playerInfo.getPlayerId();
            Bitmap transferProfileCode = QRCode. from(registerCode).bitmap();
            CustomDialogBox registerQR = new CustomDialogBox(this.getContext(), txtTransfer, transferProfileCode);
            registerQR.show();
        });

        shareProfile.setOnClickListener(v -> {
            String txtShare = "Scan to view " + playerInfo.getUsername() + "'s profile!";
            String shareCode = "qrhunt:shareprofile:" + playerInfo.getPlayerId();
            Bitmap shareProfileCode = QRCode.from(shareCode).bitmap();
            CustomDialogBox shareQR = new CustomDialogBox(this.getContext(), txtShare, shareProfileCode);
            shareQR.show();
        });


        return root;
    }
}