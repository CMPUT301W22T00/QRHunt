package com.bigyoshi.qrhunt.bottom_navigation.leaderboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.MainActivity;
import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentLeaderboardBinding;
import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.player.ProfileType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Definition: Fragment representing the leaderboard
 * Note: NA
 * Issues: This is not implemented yet
 */
public class FragmentLeaderboard extends Fragment {
    private static final String TAG = FragmentLeaderboard.class.getSimpleName();
    private List<Player> bottomLeaderboardPlayers;
    private List<Player> top3LeaderboardPlayers;
    private LeaderboardListAdapter bottomAdapter;
    private List<View> top3Views;
    private SortCriteria sortCriteria;
    private FragmentLeaderboardBinding binding;
    private FirebaseFirestore db;
    private String playerId;
    private Button sortBestUnique;
    private Button sortTotalScanned;
    private Button sortTotalScore;

    /**
     * creates instance of fragment, and handles where the activity goes after pressing back button
     * (eg, either to scanner or map)
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // really hacky but i couldn't for the life of me figure out
        // how to pass the playerid betwixt fragements using mobile_navigation.xml
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        playerId = sharedPreferences.getString("playerId", "");

        db = FirebaseFirestore.getInstance();
        sortCriteria = SortCriteria.TOTAL_SCORE;
        top3Views = new ArrayList<>();
        bottomLeaderboardPlayers = new ArrayList<>();
        top3LeaderboardPlayers = new ArrayList<>();
        getActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

    }

    private void onPlayersSnapshotTask(Task<QuerySnapshot> querySnapshotTask) {
        if (querySnapshotTask.isSuccessful() && querySnapshotTask.getResult() != null) {
            Log.d(TAG, "querying all players successful");
            for (QueryDocumentSnapshot doc : querySnapshotTask.getResult()) {
                bottomLeaderboardPlayers.add(Player.fromDoc(doc));
            }
            setSortCritera(SortCriteria.TOTAL_SCORE);
        } else {
            Log.d(TAG, "querying all players unsuccessful" + querySnapshotTask.getException());
        }
    }

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     *
     * @param inflater           inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return root
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton back = binding.leaderboardBackButton;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        top3Views.add((View) binding.layoutFirstPlace);
        top3Views.add((View) binding.layoutSecondPlace);
        top3Views.add((View) binding.layoutThirdPlace);

        sortBestUnique = binding.leaderboardSortBestUnique;
        sortBestUnique.setOnClickListener(__ -> setSortCritera(SortCriteria.BEST_UNIQUE));
        sortTotalScanned = binding.leaderboardSortTotalScanned;
        sortTotalScanned.setOnClickListener(__ -> setSortCritera(SortCriteria.TOTAL_SCANNED));
        sortTotalScore = binding.leaderboardSortTotalScore;
        sortTotalScore.setOnClickListener(__ -> setSortCritera(SortCriteria.TOTAL_SCORE));


        ListView listView = binding.leaderboardRankingList;
        bottomAdapter = new LeaderboardListAdapter(getContext(), 0, bottomLeaderboardPlayers, playerId);
        listView.setAdapter(bottomAdapter);
        db.collection("users").get().addOnCompleteListener(this::onPlayersSnapshotTask);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Player player = (Player) listView.getItemAtPosition(i);
            launchProfileView(player, playerId);
        });
        binding.leaderboardMyRankButton.setOnClickListener(__ -> {
            for (int i = 0; i < bottomLeaderboardPlayers.size(); i++) {
                if (playerId.equals(bottomLeaderboardPlayers.get(i).getPlayerId())) {
                    listView.smoothScrollToPositionFromTop(i, 0);
                    return;
                }
            }
            listView.setSelectionAfterHeaderView();
        });
        return root;
    }

    private void launchProfileView(Player playerToShow, String currentPlayerId) {
        FragmentProfile profile = new FragmentProfile();
        Bundle bundle = new Bundle();
        bundle.putSerializable("player", playerToShow);
        // admin actions not supported from leaderboard
        // the reason definitely isn't technical and has nothing to do with the fact that I
        // couldn't pass the entire player object to check if they're the admin
        // instead the reason is because it would tempt admins to delete high scoring profiles
        // in order to give themselves an edge
        if (playerId.equals(playerToShow.getPlayerId())) {
            bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.OWN_VIEW);
        } else {
            bundle.putSerializable(FragmentProfile.PROFILE_TYPE_KEY, ProfileType.VISITOR_VIEW);
        }
        bundle.putInt("isActivity", 1);
        profile.setArguments(bundle);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.leaderboard, profile, "profile")
                .addToBackStack(null)
                .commit();
    }

    private void sortLeaderboardItems() {
        // https://stackoverflow.com/questions/189559/how-do-i-join-two-lists-in-java
        List<Player> combined = Stream.concat(top3LeaderboardPlayers.stream(), bottomLeaderboardPlayers.stream()).collect(Collectors.toList());;
        combined.sort((p0, p1) -> {
            switch (sortCriteria) {
                case BEST_UNIQUE:
                    return p1.getBestUniqueQr().getScore() - p0.getBestUniqueQr().getScore();
                case TOTAL_SCORE:
                    return p1.getTotalScore() - p0.getTotalScore();
                case TOTAL_SCANNED:
                    return p1.getNumScanned() - p0.getNumScanned();
                default:
                    throw new IllegalStateException("sortCriteria not set boi");
            }
        });
        // todo: might need to only do this once?
        top3LeaderboardPlayers.clear();
        top3LeaderboardPlayers.addAll(combined.subList(0, Integer.min(3, combined.size())));
        bottomLeaderboardPlayers.clear();
        bottomLeaderboardPlayers.addAll(combined.subList(Integer.min(3, combined.size()), combined.size()));
        bottomAdapter.notifyDataSetChanged();
        updateTop3();
    }

    private void updateTop3() {
        for (int i = 0; i < top3LeaderboardPlayers.size(); i++) {
            Player player = top3LeaderboardPlayers.get(i);
            View view = top3Views.get(i);
            view.setOnClickListener(__ -> {
                launchProfileView(player, playerId);
            });
            ((TextView) view.findViewById(R.id.top_rank_username)).setText(player.getUsername());
            ((TextView) view.findViewById(R.id.top_rank_score)).setText(
                    String.format("%d points", player.getTotalScore())
            );
            ((TextView) view.findViewById(R.id.top_rank_unique)).setText(String.valueOf(player.getBestUniqueQr().getScore()));
            ((TextView) view.findViewById(R.id.top_rank_scans)).setText(String.valueOf(player.getNumScanned()));
        }
    }

    protected void setSortCritera(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
        float DISABLED_ALPHA = 0.4f;
        // theres gotta be a more efficient way, im just don't know how
        switch (sortCriteria) {
            case TOTAL_SCANNED:
                sortTotalScanned.setAlpha(1);
                sortBestUnique.setAlpha(DISABLED_ALPHA);
                sortTotalScore.setAlpha(DISABLED_ALPHA);
                break;
            case TOTAL_SCORE:
                sortTotalScore.setAlpha(1);
                sortBestUnique.setAlpha(DISABLED_ALPHA);
                sortTotalScanned.setAlpha(DISABLED_ALPHA);
                break;
            case BEST_UNIQUE:
                sortBestUnique.setAlpha(1f);
                sortTotalScore.setAlpha(DISABLED_ALPHA);
                sortTotalScanned.setAlpha(DISABLED_ALPHA);
                break;
        }
        sortLeaderboardItems();
    }

    /**
     * Destroys the view and makes binding null
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}