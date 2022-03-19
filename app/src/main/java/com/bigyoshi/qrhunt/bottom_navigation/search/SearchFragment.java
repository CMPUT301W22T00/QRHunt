package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.Player;
import com.bigyoshi.qrhunt.databinding.FragmentSearchBinding;

import org.osmdroid.config.Configuration;

/**
 * Definition: Fragment representing the search function (searching users)
 * Note: NA
 * Issues: Not implemented yet
 */
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private Player player;
    private int navId;

    public SearchFragment(Player player, int id) {
        this.player = player;
        this.navId = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        Bundle prevNav = new Bundle();
                        prevNav.putSerializable("previous", navId);
                        intent.putExtras(prevNav);
                        startActivity(intent);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;

    }
}
