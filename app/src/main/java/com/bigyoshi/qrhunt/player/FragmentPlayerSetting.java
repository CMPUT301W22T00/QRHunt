package com.bigyoshi.qrhunt.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsBinding;

/**
 * Definition: Settings menu for editing user's profile and generating QR to access account on other devices
 * Note: NA
 * Issues: Currently does not implement generating a log-in QR code
 */
public class FragmentPlayerSetting extends Fragment {
    private FragmentUserSettingsBinding binding;
    private Player playerInfo;
    private TextView playerProfileSettings;
    private ImageView backButton;

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
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        playerProfileSettings = root.findViewById(R.id.player_settings_edit_profile_clickable);
        backButton = root.findViewById(R.id.player_settings_back_button);

<<<<<<< HEAD
        playerProfileSettings.setOnClickListener(v -> {
            DialogFragment dialogFragment = new FragmentPlayerProfileSetting();
            Bundle bundle = new Bundle();
            bundle.putSerializable("player", playerInfo);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getChildFragmentManager(), null);
=======
        playerProfileSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentPlayerProfileSetting profileSetting =
                        new FragmentPlayerProfileSetting(playerInfo);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.player_settings, profileSetting, "setting");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle result = new Bundle();
                result.putSerializable("info", playerInfo);
                getParentFragmentManager().setFragmentResult("getInfo", result);
                getActivity().getSupportFragmentManager().popBackStack();
            }
>>>>>>> main
        });

        backButton.setOnClickListener(view -> {
            Bundle result = new Bundle();
            result.putSerializable("info", playerInfo);
            getParentFragmentManager().setFragmentResult("getInfo", result);
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return root;
    }
}
