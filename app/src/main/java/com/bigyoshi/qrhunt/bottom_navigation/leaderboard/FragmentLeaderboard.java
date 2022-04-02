package com.bigyoshi.qrhunt.bottom_navigation.leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Definition: Fragment representing the leaderboard
 * Note: NA
 * Issues: This is not implemented yet
 */
public class FragmentLeaderboard extends Fragment {
    private static final String TAG = FragmentLeaderboard.class.getSimpleName();
    private List<Map<String, Object>> bottomLeaderboardPlayers;
    private List<Map<String, Object>> top3LeaderboardPlayers;
    private LeaderboardListAdapter bottomAdapter;
    private List<View> top3Views;
    private SortCriteria sortCriteria;
    private FragmentLeaderboardBinding binding;
    private FirebaseFirestore db;

    /**
     * creates instance of fragment, and handles where the activity goes after pressing back button
     * (eg, either to scanner or map)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (querySnapshotTask.isSuccessful() &&  querySnapshotTask.getResult() != null) {
            Log.d(TAG,"querying all players successful");
            for (QueryDocumentSnapshot doc : querySnapshotTask.getResult()){
                // ideally, we could just toObject our way to freedom,
                // but that's not really an option, out tech debt is too deep, deadlines too tight
                // constrainsts too constrained
                Map<String, Object> playerInfo = new HashMap<String, Object>();
                playerInfo.put("username", doc.get("username"));
                playerInfo.put("totalScore", Optional.ofNullable(doc.get("totalScore")).orElse(0L));
                playerInfo.put("bestUniqueQr", Optional.ofNullable(doc.get("bestUniqueQr.score")).orElse(0L));
                playerInfo.put("numScanned", Optional.ofNullable(doc.get("totalScanned")).orElse(0L));
                bottomLeaderboardPlayers.add(playerInfo);
            }
            sortLeaderboardItems();
        } else {
            Log.d(TAG,"querying all players unsuccessful" + querySnapshotTask.getException());
        }
    }

    /**
     * Sets up fragment to be loaded in, finds all views, sets onClickListener for buttons
     * @param inflater inflater
     * @param container Where the fragment is contained
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

        ListView listView = binding.leaderboardRankingList;
        bottomAdapter = new LeaderboardListAdapter(getContext(), 0, bottomLeaderboardPlayers);
        listView.setAdapter(bottomAdapter);
        db.collection("users").get().addOnCompleteListener(this::onPlayersSnapshotTask);

        return root;
    }

    private void sortLeaderboardItems() {
        // https://stackoverflow.com/questions/189559/how-do-i-join-two-lists-in-java
        List<Map<String, Object>> combined = Stream.concat(top3LeaderboardPlayers.stream(), bottomLeaderboardPlayers.stream()).collect(Collectors.toList());;
        combined.sort((hm0, hm1) -> {
            switch (sortCriteria) {
                case BEST_UNIQUE:
                    return Math.toIntExact((Long) hm1.get("bestUniqueQr") - (Long) hm0.get("bestUniqueQr"));
                case TOTAL_SCORE:
                    return Math.toIntExact((Long) hm1.get("totalScore") - (Long) hm0.get("totalScore"));
                case NUM_SCANNED:
                    return Math.toIntExact((Long) hm1.get("numScanned") - (Long) hm0.get("numScanned"));
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
            Map<String, Object> playerInfo = top3LeaderboardPlayers.get(i);
            View view = top3Views.get(i);
            ((TextView) getActivity().findViewById(R.id.top_rank_num)).setText(String.valueOf(i + 1));
            ((TextView) getActivity().findViewById(R.id.top_rank_username)).setText((String) playerInfo.get("username"));
            ((TextView) getActivity().findViewById(R.id.top_rank_score)).setText(String.valueOf((Long) playerInfo.get("totalScore")));
            ((TextView) getActivity().findViewById(R.id.top_rank_unique)).setText(String.valueOf((Long) playerInfo.get("bestUniqueQr")));
            ((TextView) getActivity().findViewById(R.id.top_rank_scans)).setText(String.valueOf((Long) playerInfo.get("numScanned")));

        }
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