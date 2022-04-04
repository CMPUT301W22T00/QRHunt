package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchClient {
    private final String TAG = SearchClient.class.getSimpleName();
    private final String username;
    private SearchClient.UsernameResultsFound onSearchResults;
    private boolean cancelled;
    private String userScore;

    public SearchClient(String usernameQuery) {
        this.username = usernameQuery;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setOnSearchResults(SearchClient.UsernameResultsFound onSearchResults) {
        this.onSearchResults = onSearchResults;
    }

    public void scheduleSearchQuery() {
        int VERIFICATION_DELAY = 500;
        // Full text search isn't supported that would be too easy.
        // Instead, we can hack around it with a prefix search
        String usernameBoundary = username.substring(0, username.length() - 1) + Character.toChars(Character.codePointAt(username, username.length() - 1) + 1)[0];
        Log.d(TAG, String.format("scheduling to search for \"%s\" with end at \"%s\" in %d ms", username, usernameBoundary, VERIFICATION_DELAY));
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cancelled) {
                            if (username.isEmpty()) {
                                // don't allow/verify empty usernames
                                onSearchResults.onResults(null);
                            } else {
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .whereGreaterThanOrEqualTo("username", username)
                                        .whereLessThan("username", usernameBoundary)
                                        .get()
                                        .addOnCompleteListener(
                                                qSnapshot -> {
                                                    if (cancelled) {
                                                        return;
                                                    }
                                                    if (qSnapshot.isSuccessful()) {
                                                        onSearchResults.onResults(qSnapshot.getResult().getDocuments());
                                                    } else {
                                                        onSearchResults.onResults(new ArrayList<>());
                                                    }
                                                });
                            }
                        }
                    }
                }, VERIFICATION_DELAY);
    }

    public static interface UsernameResultsFound {
        public void onResults(List<DocumentSnapshot> found);
    }
}
