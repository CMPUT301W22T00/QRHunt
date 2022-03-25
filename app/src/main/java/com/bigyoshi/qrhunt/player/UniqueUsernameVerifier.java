package com.bigyoshi.qrhunt.player;

import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

public class UniqueUsernameVerifier {
    private final String username;
    private final View checkmark;
    private final Button saveButton;
    private final String playerId;
    private boolean cancelled;

    public UniqueUsernameVerifier(
            String username, String playerId, View checkmark, Button saveButton) {
        this.username = username;
        this.playerId = playerId;
        this.checkmark = checkmark;
        this.saveButton = saveButton;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void checkUsername() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("username", "<actual username here>")
                .whereNotEqualTo(FieldPath.documentId(), username)
                .get()
                .addOnCompleteListener(
                        qSnapshot -> {
                            if (qSnapshot.isSuccessful() && !isCancelled()) {
                                if (qSnapshot.getResult().isEmpty()) {
                                    // enable the save button, show checkmark icon etc
                                } else {
                                    // show x, callout "username is taken",
                                    // keep save button disabled
                                }
                            }
                        });
    }
}
