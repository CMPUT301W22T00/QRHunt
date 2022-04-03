package com.bigyoshi.qrhunt.qr;

import com.bigyoshi.qrhunt.player.Player;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class QRComment implements Serializable {
    private static final String TAG = QRComment.class.getSimpleName();
    protected String username;
    protected String comment;

    public QRComment() {}

    public QRComment(String comment, String username) {
        this.comment = comment;
        this.username = username;
    }

    @Exclude
    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    @Exclude
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
