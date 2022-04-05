package com.bigyoshi.qrhunt.player;

import android.util.Log;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Definition: Veries if the username is unique when user wants a new username
 * Note: N/A
 * Issues: N/A
 */
public class UniqueUsernameVerifier {
    private final String TAG = UniqueUsernameVerifier.class.getSimpleName();
    private final Integer VERIFICATION_DELAY = 500;
    private final String username;
    private final String playerId;
    private OnUsernameVerificationResults onUsernameVerificationResults;
    private boolean cancelled;

    /**
     * Constructor method
     *
     * @param username Username
     * @param playerId Player id
     */
    public UniqueUsernameVerifier(String username, String playerId) {
        this.username = username;
        this.playerId = playerId;
    }

    /**
     * Detects if the user cancelled the request
     */
    public void cancel() {
        cancelled = true;
    }

    /**
     * Checks if the user cancelled their request
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Checks condition of unique username
     *
     * @param onUsernameVerificationResults unique
     */
    public void setOnUsernameVerificationResults(OnUsernameVerificationResults onUsernameVerificationResults) {
        this.onUsernameVerificationResults = onUsernameVerificationResults;
    }

    /**
     * Queries to check if the unique name is unique
     */
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

    /**
     * Interface for the unique for results
     */
    public static interface OnUsernameVerificationResults {
        public void onResults(boolean unique);
    }
}
