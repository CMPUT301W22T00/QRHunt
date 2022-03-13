package com.bigyoshi.qrhunt;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bigyoshi.qrhunt.databinding.FragmentUserSettingsBinding;

import org.osmdroid.config.Configuration;

public class FragmentPlayerSetting extends Fragment {
    private FragmentUserSettingsBinding binding;
    private PlayerInfo playerInfo;
    private TextView playerProfileSettings;

    public FragmentPlayerSetting(PlayerInfo playerInfo){
        this.playerInfo = playerInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentUserSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        playerProfileSettings = root.findViewById(R.id.settings_edit_profile);

        playerProfileSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentPlayerProfileSetting profileSetting = new FragmentPlayerProfileSetting(playerInfo);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.playerSettings, profileSetting, "setting");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return root;
    }
}
