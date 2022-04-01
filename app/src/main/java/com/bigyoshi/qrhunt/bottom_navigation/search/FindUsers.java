package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.util.Log;

import com.bigyoshi.qrhunt.player.UniqueUsernameVerifier;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FindUsers {
    private final String TAG = FindUsers.class.getSimpleName();
    private final Integer VERIFICATION_DELAY = 500;
    private final String username;
    private final String playerId;
    private FindUsers.UsernameResultsFound onUsernameVerificationResults;
    private boolean cancelled;
    private String userScore;

    public FindUsers(String username, String playerId) {
        this.username = username;
        this.playerId = playerId;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setOnUsernameVerificationResults(FindUsers.UsernameResultsFound onUsernameVerificationResults) {
        this.onUsernameVerificationResults = onUsernameVerificationResults;
    }

    public void scheduleUniqueUsernameVerification() {
        Log.d(TAG, String.format("scheduling to run verification on \"%s\" in %d ms", username, VERIFICATION_DELAY));
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cancelled) {
                            if (username.isEmpty()) {
                                // don't allow/verify empty usernames
                                onUsernameVerificationResults.onResults(null);
                            } else {
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .whereEqualTo("username", username)
                                        .get()
                                        .addOnCompleteListener(
                                                qSnapshot -> {
                                                    if (qSnapshot.isSuccessful()
                                                            && !isCancelled()) {
                                                        if (qSnapshot.getResult().isEmpty()) {
                                                            onUsernameVerificationResults
                                                                    .onResults(null);
                                                            return;
                                                        }

                                                    }
                                                    List<DocumentSnapshot> resultList = qSnapshot.getResult().getDocuments();
                                                    onUsernameVerificationResults.onResults(
                                                            resultList);

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
