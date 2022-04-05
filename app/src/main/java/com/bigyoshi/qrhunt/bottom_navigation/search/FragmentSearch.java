package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentSearchBinding;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.bigyoshi.qrhunt.qr.FragmentScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


/**
 * Definition: Fragment representing the search function (searching users)
 * Note: N/A
 * Issues: Is case sensitive
 */
public class FragmentSearch extends Fragment {
    private static final String TAG = FragmentSearch.class.getSimpleName();
    private FragmentSearchBinding binding;
    private Player player;
    private int navId;
    private ImageButton back;
    private EditText searchBar;
    private ProgressBar searchProgressBar;
    private SearchClient searchClient;
    private ListView searchResults;
    private ArrayList<Player> searchList = new ArrayList<>();
    private ArrayAdapter<Player> searchAdapter;

    /**
     * Constructor method
     *
     * @param player player
     * @param id id
     */
    public FragmentSearch(Player player, int id) {
        this.player = player;
        this.navId = id;
    }

    /**
     * Creates back pressed functionality for the search bar
     *
     * @param savedInstanceState savedInstanceState
     */
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

    /**
     * Creates the view for the search bar
     *
     * @param inflater            inflater
     * @param container           where the fragment is contained
     * @param savedInstanceState  savedInstanceState
     * @return root
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        back = root.findViewById(R.id.search_bar_back_button);
        searchProgressBar = root.findViewById(R.id.search_bar_progress_bar);
        searchResults = root.findViewById(R.id.search_bar_results_list);
        searchAdapter = new SearchList(getContext(), searchList);
        searchResults.setAdapter(searchAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                FragmentScanner.codeScanner.setScanMode(ScanMode.SINGLE);
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
                        searchAdapter.clear();
                        // later enabled by check username
                        if (charSequence != player.getUsername()) {
                            searchProgressBar.setVisibility(View.VISIBLE);
                        }

                        if (searchClient != null) {
                            searchClient
                                    .cancel(); // make sure we aren't getting old results late that
                            // enable the save button
                        }
                        if (charSequence.length() > 0) {
                            searchClient = new SearchClient(charSequence.toString());
                            searchClient.setOnSearchResults(
                                    docs -> {
                                        if (docs.size() == 0){
                                            root.findViewById(R.id.search_no_results_text)
                                                    .setVisibility(View.VISIBLE);
                                        } else {
                                            root.findViewById(R.id.search_no_results_text)
                                                    .setVisibility(View.INVISIBLE);
                                        }
                                        Log.d(TAG, String.format("Found %d users for search query %s", docs.size(), searchClient));
                                        for (DocumentSnapshot doc : docs) {
                                            Player found = Player.fromDoc(doc);
                                            found.setPlayerId(doc.getId());
                                            searchAdapter.add(found);
                                        }
                                        searchProgressBar.setVisibility(View.INVISIBLE);
                                    });
                            searchClient.scheduleSearchQuery();
                        } else {
                            root.findViewById(R.id.search_no_results_text)
                                    .setVisibility(View.INVISIBLE);
                            searchProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> searchAdapter, View view, int i, long l) {
                FragmentProfile profile = new FragmentProfile();

                Bundle bundle = new Bundle();

                // todo: actually make this make sense and act on it
                if ( ((Player)searchAdapter.getItemAtPosition(i)).getPlayerId() == (player.getPlayerId())) {
                    bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.OWN_VIEW);
                }
                else if (player.isAdmin()){
                    bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.ADMIN_VIEW);
                }
                else{
                    bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.VISITOR_VIEW);

                }
                bundle.putSerializable("player", (Player) searchAdapter.getItemAtPosition(i));
                bundle.putSerializable("selfPlayer", player);
                bundle.putSerializable("isActivity", 0);
                profile.setArguments(bundle);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.search_bar, profile, "profile")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return root;

    }
}
