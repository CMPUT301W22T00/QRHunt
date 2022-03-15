package com.bigyoshi.qrhunt.bottom_navigation.rank_board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentLeaderboardBinding;

import org.osmdroid.config.Configuration;


/**
 *  Definition: List for ranks setup
 *
 *
 */
public class RankBoardFragment extends Fragment {
    private ImageButton back;

    private FragmentLeaderboardBinding binding;

    /**
     * creates instance of fragment, and handles where the activity goes after pressing back button
     * (eg, either to scanner or map)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        back = binding.buttonBack;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    /**
     * Invokes parent method and...
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}