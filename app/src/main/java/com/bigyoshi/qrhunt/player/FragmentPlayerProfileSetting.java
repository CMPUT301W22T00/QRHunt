package com.bigyoshi.qrhunt.player;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsEditProfileBinding;

/**
 * Definition: Setting for user to edit their account information (username, email, social handle)
 * Note: N/A
 * Issues: N/A
 */
public class FragmentPlayerProfileSetting extends DialogFragment {
    private static final String TAG = FragmentPlayerProfileSetting.class.getSimpleName();
    private Player playerInfo;
    private EditText username;
    private ProgressBar usernameProgressBar;
    private EditText email;
    private EditText socials;
    private Button ok;
    private Button cancel;
    private UniqueUsernameVerifier verifier;

    private FragmentUserSettingsEditProfileBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog_Custom);
    }

    /**
     * Creates view and handles user button clicks within UI
     *
     * @param inflater           inflater
     * @param container          where the fragment is contained
     * @param savedInstanceState savedInstanceState
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
        // https://stackoverflow.com/questions/8045556/cant-make-the-custom-dialogfragment-transparent-over-the-fragment
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        username = root.findViewById(R.id.player_profile_settings_edit_username);
        username.setText(playerInfo.getUsername());
        email = root.findViewById(R.id.player_profile_settings_edit_email);
        email.setText(playerInfo.getContact().getEmail());
        socials = root.findViewById(R.id.player_profile_settings_edit_social);
        socials.setText(playerInfo.getContact().getSocial());
        ok = root.findViewById(R.id.player_profile_settings_ok_button);
        cancel = root.findViewById(R.id.player_profile_settings_cancel_button);
        usernameProgressBar = root.findViewById(R.id.player_profile_settings_edit_username_progress_bar);

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
                            usernameProgressBar.setVisibility(View.VISIBLE);
                        }

                        if (verifier != null) {
                            verifier
                                    .cancel(); // make sure we aren't getting old results late that
                                               // enable the save button
                        }
                        if (charSequence.length() > 0) {
                            verifier =
                                    new UniqueUsernameVerifier(
                                            charSequence.toString(), playerInfo.getPlayerId());
                            verifier.setOnUsernameVerificationResults(
                                    isUnique -> {
                                        if (isUnique) {
                                            Log.d(TAG,charSequence + " determined to be unique");
                                            ok.setAlpha(1);
                                        } else {
                                            Log.d(TAG,charSequence + " determined to be NOT unique");
                                            username.setError("Username isn't available");
                                        }
                                        usernameProgressBar.setVisibility(View.INVISIBLE);
                                        ok.setEnabled(isUnique);
                                    });
                            verifier.scheduleUniqueUsernameVerification();
                        } else {
                            username.setError("Username is required");
                            usernameProgressBar.setVisibility(View.INVISIBLE);
                        }

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
                playerInfo.updateDB();
                dismiss();
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
