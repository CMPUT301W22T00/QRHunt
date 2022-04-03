package com.bigyoshi.qrhunt.qr;

import java.io.Serializable;

public class QRComment implements Serializable {
    private String username;
    private String comment;

    public QRComment(String comment, String username) {
        this.comment = comment;
        this.username = username;
    }

    public String getComment() { return comment; }

    public String getUsername() { return username; }
}
