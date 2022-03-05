package com.bigyoshi.qrhunt;

import java.util.ArrayList;

public class QRProfile {
    // Still need to decide on the actual type, for now, I'll make them String
    private QRCode QRRepresent;
    private ArrayList<String> comments;  // Confirmed to be the type?
    private String image; // Need JPEG?
    private Boolean hideLoc;
    private Boolean isHighestValue;

    // Need to check if they added a photo or not
    public QRProfile(QRCode q, Boolean toHide){
        QRRepresent = q;
        // No image -- set to default image
        hideLoc = toHide;
        // Need to find a way to check if it is the highesValuedQRCode the player has
    }

    public QRProfile(QRCode q, String i, Boolean toHide){
        QRRepresent = q;
        image = i;
        hideLoc = toHide;
        // Need to find a way to check if it is the highesValuedQRCode the player has -- check db
    }

    public void addComment(String newComment){
        comments.add(newComment);
    }

    public void updateIsHighest(){
        this.isHighestValue = false;
    }

    public void updatePrivateLoc(Boolean currentSetting){
        hideLoc = !hideLoc;
    }

    public String deleteQRProfile(){
        // Delete a QRProfile -- need to know which QRLibrary (which Player) and if it is the Player who wants it deleted (owner) or an admin
        return "";
    }


}
