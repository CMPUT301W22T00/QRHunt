package com.bigyoshi.qrhunt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsEditProfileBinding;

import org.osmdroid.config.Configuration;


public class FragmentPlayerProfileSetting extends DialogFragment {
    EditText username;
    EditText email;
    EditText socials;
    PlayerInfo playerInfo;

    public FragmentPlayerProfileSetting(PlayerInfo playerInfo){
        this.playerInfo = playerInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        return getLayoutInflater().inflate(R.layout.fragment_user_settings_edit_profile, null);
    }
}
