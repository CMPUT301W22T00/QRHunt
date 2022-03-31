package com.bigyoshi.qrhunt.player;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsEditProfileBinding;

import org.osmdroid.config.Configuration;

/**
 * Definition: Setting for user to edit their account information (username, email, social handle)
 * Note: NA
 * Issues: NA
 */
public class FragmentPlayerProfileSetting extends DialogFragment {
    private static final String TAG = FragmentPlayerProfileSetting.class.getSimpleName();
    private Player playerInfo;
    private EditText username;
    private EditText email;
    private EditText socials;
    private Button ok;
    private Button cancel;
    private ImageView checkValidUsername;
    private UniqueUsernameVerifier verifier;

    private FragmentUserSettingsEditProfileBinding binding;

    /**
     *  Constructor method
     *
     *  Note: Invokes parent constructor
     */
    public FragmentPlayerProfileSetting(){
        super();
    }

    /**
     * Creates view and handles user button clicks within UI
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
        binding = FragmentUserSettingsEditProfileBinding.inflate(inflater,
                container,
                false);
        View root = binding.getRoot();

        username = root.findViewById(R.id.player_profile_settings_edit_username);
        username.setText(playerInfo.getUsername());
        email = root.findViewById(R.id.player_profile_settings_edit_email);
        email.setText(playerInfo.getContact().getEmail());
        socials = root.findViewById(R.id.player_profile_settings_edit_social);
        socials.setText(playerInfo.getContact().getSocial());
        ok = root.findViewById(R.id.player_profile_settings_ok_button);
        cancel = root.findViewById(R.id.player_profile_settings_cancel_button);
        checkValidUsername = root.findViewById(R.id.player_profile_settings_validator);

        username.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // later enabled by check username
                        if (charSequence != playerInfo.getUsername()) {
                            ok.setEnabled(false);
                            ok.setAlpha(0.5f);
                        }

                        if (verifier != null) {
                            verifier
                                    .cancel(); // make sure we aren't getting old results late that
                                               // enable the save button
                        }
                        verifier =
                                new UniqueUsernameVerifier(
                                        charSequence.toString(), playerInfo.getPlayerId());
                        verifier.setOnUsernameVerificationResults(
                                isUnique -> {
                                    if (isUnique) {
                                        Log.d(TAG,charSequence.toString() + " determined to be unique");
                                        ok.setAlpha(1);
                                    } else {
                                        Log.d(TAG,charSequence.toString() + " determined to be NOT unique");
                                        Toast.makeText(getActivity(), "Username isn't unique!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    ok.setEnabled(isUnique);
                                });
                        verifier.scheduleUniqueUsernameVerification();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!username.getText().toString().matches("")){
                    playerInfo.setUsername(username.getText().toString());
                }
                if (!email.getText().toString().matches("")){
                    playerInfo.getContact().setEmail(email.getText().toString());
                }
                if (!socials.getText().toString().matches("")){
                    playerInfo.getContact().setSocial(socials.getText().toString());
                }
                Bundle result = new Bundle();
                result.putSerializable("newInfo", playerInfo);
                getParentFragmentManager().setFragmentResult("getNewInfo", result);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return root;
    }
}
