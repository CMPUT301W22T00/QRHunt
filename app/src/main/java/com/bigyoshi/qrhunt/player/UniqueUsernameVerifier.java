package com.bigyoshi.qrhunt.player;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

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

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setOnUsernameVerificationResults(OnUsernameVerificationResults onUsernameVerificationResults) {
        this.onUsernameVerificationResults = onUsernameVerificationResults;
    }

    public void scheduleUniqueUsernameVerification() {
        Log.d(TAG, String.format("scheduling to run verification on \"%s\" in %d ms", username, VERIFICATION_DELAY));
        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cancelled && !username.isEmpty()) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .whereNotEqualTo("username", "<actual username here>")
                                    .whereNotEqualTo(FieldPath.documentId(), (String) username)
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
                                                else if (!qSnapshot.getResult().isEmpty()) {
                                                    onUsernameVerificationResults.onResults(
                                                            false);
                                                }
                                            });
                        }
                    }
                }, VERIFICATION_DELAY);
    }

    public static interface OnUsernameVerificationResults {
        public void onResults(boolean unique);
    }
}
