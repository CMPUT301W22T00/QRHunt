package com.bigyoshi.qrhunt.bottom_navigation.search;

import android.util.Log;

import com.bigyoshi.qrhunt.player.UniqueUsernameVerifier;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class FindUsers {
    private final String TAG = FindUsers.class.getSimpleName();
    private final Integer VERIFICATION_DELAY = 500;
    private final String username;
    private final String playerId;
    private FindUsers.UsernameResultsFound onUsernameVerificationResults;
    private boolean cancelled;

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
                                onUsernameVerificationResults.onResults(false);
                            } else {
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .whereEqualTo("username", username)
                                        .whereNotEqualTo(FieldPath.documentId(), playerId)
                                        .get()
                                        .addOnCompleteListener(
                                                qSnapshot -> {
                                                    if (qSnapshot.isSuccessful()
                                                            && !isCancelled()) {
                                                        if (qSnapshot.getResult().isEmpty()) {
                                                            onUsernameVerificationResults
                                                                    .onResults(true);
                                                            return;
                                                        }

                                                    }
                                                    onUsernameVerificationResults.onResults(
                                                            false);
                                                });
                            }
                        }
                    }
                }, VERIFICATION_DELAY);
    }

    public static interface UsernameResultsFound {
        public void onResults(boolean unique);
    }
}
