package com.bigyoshi.qrhunt;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsEditProfileBinding;

import org.osmdroid.config.Configuration;

/**
 * Definition: Setting for user to edit their account information (username, email, socials handle)
 *
 *
 */
public class FragmentPlayerProfileSetting extends DialogFragment {
    private Player playerInfo;
    private EditText username;
    private EditText email;
    private EditText socials;
    private Button ok;
    private Button cancel;

    private FragmentUserSettingsEditProfileBinding binding;

    /**
     * Constructor
     * @param playerInfo
     */
    public FragmentPlayerProfileSetting(Player playerInfo){
        this.playerInfo = playerInfo;
    }

    /**
     *  Invokes parent constructor
     */
    public FragmentPlayerProfileSetting(){
        super();
    }

    /**
     *  Creates view and handles user button clicks
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentUserSettingsEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        username = root.findViewById(R.id.edit_username);
        email = root.findViewById(R.id.edit_email);
        socials = root.findViewById(R.id.edit_socials);
        ok = root.findViewById(R.id.ok_edit_profile);
        cancel = root.findViewById(R.id.cancel_edit_profile);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!username.getText().toString().matches("")){
                    playerInfo.setUsername(username.getText().toString());
                } else if (!email.getText().toString().matches("")){
                    playerInfo.getContact().setEmail(email.getText().toString());
                } else if (!socials.getText().toString().matches("")){
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return root;
    }
}
