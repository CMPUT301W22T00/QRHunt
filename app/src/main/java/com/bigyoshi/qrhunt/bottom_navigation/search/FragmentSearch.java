package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.player.FragmentPlayerProfileSetting;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentSearchBinding;
import com.bigyoshi.qrhunt.player.UniqueUsernameVerifier;


/**
 * Definition: Fragment representing the search function (searching users)
 * Note: NA
 * Issues: Not implemented yet
 */
public class FragmentSearch extends Fragment {
    private static final String TAG = FragmentSearch.class.getSimpleName();
    private FragmentSearchBinding binding;
    private Player player;
    private int navId;
    private ImageButton back;
    private EditText searchBar;
    private ProgressBar searchProgressBar;
    private FindUsers verifier;

    public FragmentSearch(Player player, int id) {
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
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        back = root.findViewById(R.id.search_bar_back_button);
        searchProgressBar = root.findViewById(R.id.search_bar_progress_bar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        searchBar = root.findViewById(R.id.search_bar_search_edit_text);
        // https://stackoverflow.com/questions/1489852/android-handle-enter-in-an-edittext
        searchBar.setImeActionLabel("Press enter for search", KeyEvent.KEYCODE_ENTER);

        searchBar.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // later enabled by check username
                        if (charSequence != player.getUsername()) {
                            searchProgressBar.setVisibility(View.VISIBLE);
                        }

                        if (verifier != null) {
                            verifier
                                    .cancel(); // make sure we aren't getting old results late that
                            // enable the save button
                        }
                        if (charSequence.length() > 0) {
                            verifier =
                                    new FindUsers(
                                            charSequence.toString(), player.getPlayerId());
                            verifier.setOnUsernameVerificationResults(
                                    isUnique -> {
                                        if (isUnique) {
                                            Log.d(TAG,charSequence + " determined to be no existing user");
                                        } else {
                                            Log.d(TAG,charSequence + " determined to be an existing user");
                                        }
                                        searchProgressBar.setVisibility(View.INVISIBLE);
                                    });
                            verifier.scheduleUniqueUsernameVerification();
                        } else {
                            searchProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });



        return root;

    }
}
