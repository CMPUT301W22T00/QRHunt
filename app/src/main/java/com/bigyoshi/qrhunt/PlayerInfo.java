package com.bigyoshi.qrhunt;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Random;

public class PlayerInfo {
    private int QRTotal;
    private int QRTotalScanned;
    private String username;
    private int QRTotalRank;
    private int highestValueQRRank;
    private Contact contact;
    private Boolean admin;

    public PlayerInfo(Context context){
        // Automatically generate a random unique username (changed later by Player if they want)
        // QRTotalRank and highestValueQRRank need to be set to the lowest rank so far
        // Contact initialization needed
        this.QRTotal = 0;
        this.QRTotalScanned = 0;
        this.username = generateUsername(context);
        this.admin = false;
        this.contact = new Contact();
        this.QRTotalRank = 1; // NOT FINAL, WE NEED TO FIND THE LOWEST RANK FOR A NEW PLAYER
        this.highestValueQRRank = 1; // AGAIN, NOT FINAL
        // How will we decide who is an admin?
    }

    public PlayerInfo(Integer qrTotalRank, Integer qrtotalScanned, HashMap<String,String> contactToUse, Boolean admin,
                      Integer qrtotal, Integer highestValueQRRank, String username) {
        this.QRTotalRank = qrTotalRank;
        this.QRTotalScanned = qrtotalScanned;
        this.contact = new Contact();
        this.contact.updateSocial(contactToUse.get("social"));
        this.contact.updateEmail(contactToUse.get("email"));
        this.admin = admin;
        this.QRTotal = qrtotal;
        this.highestValueQRRank = highestValueQRRank;
        this.username = username;
    }

    public Contact getContact(){
        return this.contact;
    }

    public int getQRTotalScanned(){
        return this.QRTotalScanned;
    }

    public int getQRTotal(){
        return this.QRTotal;
    }

    public String getUsername(){
        return this.username;
    }

    public int getQRTotalRank(){
        return this.QRTotalRank;
    }

    public int getHighestValueQRRank(){
        return this.highestValueQRRank;
    }

    public void updateRankTotal(){
        // Needs to take in the parameter Firebase PlayerBase (in order to find the Player and update the ranks of all players in the db)
        // Needs to take in the new total to compare in the ranks
    }

    public void updateRankHighestValue(){
        // Takes in Firebase QRBase to update the Player's highest valued QR rank
        // May need to take in the new QR code to update it to the unique on for the Player and to compare it against the ranks
    }

    public void updateContact(String editTextId, String toUpdate){
        // Use the editTextId to identify which contact to update (with toUpdate)
    }

    public void updateUsername(String newName){
        this.username = newName;
    }

    public Boolean isAdmin(){
        return admin;
    }

    public String deletePlayerInfo(PlayerInfo playerInfoToDelete){
        if (this.isAdmin()){
            // Delete PlayerInfo
            return "PlayerInfo successfully deleted.";
        } else {
            return "PlayerInfo was not deleted.";
        }
    }

    public String makeAdmin(PlayerInfo newAdmin, PlayerInfo approvingAdmin){
        if (approvingAdmin.isAdmin()){
            newAdmin.admin = true;
            return "PlayerInfo now admin.";
        } else {
            newAdmin.admin = false;
            return "PlayerInfo did not become admin.";
        }
    }

    public String generateQRLogIn(){
        // May not be a string
        // Must return a QR Code representing the player's log-in specifications -- just the uniqueKey (hopefully)
        return "";
    }

    public String generateGameStatus(){
        // May not be a string
        // Must return a QR Code with Player's username, QR total, and rankings
        return "";
    }

    public String generateUsername(Context context){
        // Random unique username generated when account is first created
        Random rand = new Random();
        Resources res = context.getResources();
        String[] adj = res.getStringArray(R.array.adjectives);
        String[] noun = res.getStringArray(R.array.noun);
        String adjName = adj[rand.nextInt(adj.length - 1)];
        String nounName = noun[rand.nextInt(noun.length - 1)];
        int upperbound = 100;
        String numName = Integer.toString(rand.nextInt(upperbound));
        String newName = adjName + nounName + numName;
        return newName;
    }
}
