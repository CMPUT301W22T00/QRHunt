package com.bigyoshi.qrhunt.qr;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/**
 * Definition: Represents the comments on QR profiles
 * Note: N/A
 * Issues: N/A
 */
public class QRComment implements Serializable {
    private static final String TAG = QRComment.class.getSimpleName();
    protected String username;
    protected String comment;

    public QRComment() {} // and this...? Delete if it does nothing <-------------------

    /**
     * Constructor method
     *
     * @param comment   comment made
     * @param username  user who made the comment
     */
    public QRComment(String comment, String username) {
        this.comment = comment;
        this.username = username;
    }

    /**
     * Getter method
     *
     * @return comment
     */
    @Exclude
    public String getComment() { return comment; }

    /**
     * Setter method
     *
     * @param comment comment
     */
    public void setComment(String comment) { this.comment = comment; }

    /**
     * Getter method
     *
     * @return username
     */
    @Exclude
    public String getUsername() { return username; }

    /**
     * Setter method
     *
     * @param username username
     */
    public void setUsername(String username) { this.username = username; }
}
