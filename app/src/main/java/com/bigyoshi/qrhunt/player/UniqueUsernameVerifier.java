package com.bigyoshi.qrhunt.player;

import android.util.Log;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Definition: todo smth smth smth
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

    public UniqueUsernameVerifier(String username, String playerId) {
        this.username = username;
        this.playerId = playerId;
    }

    /**
     * todo does smth
     */
    public void cancel() {
        cancelled = true;
    }

    /**
     * todo does smth
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * todo does smth
     *
     * @param onUsernameVerificationResults
     */
    public void setOnUsernameVerificationResults(OnUsernameVerificationResults onUsernameVerificationResults) {
        this.onUsernameVerificationResults = onUsernameVerificationResults;
    }

    /**
     * todo does smth
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
     * todo does smth
     */
    public static interface OnUsernameVerificationResults {
        public void onResults(boolean unique);
    }
}
