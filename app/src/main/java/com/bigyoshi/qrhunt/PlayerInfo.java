package com.bigyoshi.qrhunt;

public class PlayerInfo {
    private int QRTotal;
    private String username;
    private int QRTotalRank;
    private int highestValueQRRank;
    private Contact contact;
    private Boolean admin;
    private String uniqueKey; // May not be a String; used to identify players (log-in specs)

    public PlayerInfo(){
        // Automatically generate a random unique username (changed later by Player if they want)
        // QRTotalRank and highestValueQRRank need to be set to the lowest rank so far
        // Contact initialization needed
        // Automatically generate the uniqueKey (not visible to the Player)
        this.QRTotal = 0;
        this.username = generateUsername();
        this.uniqueKey = generateUniqueKey();
        // How will we decide who is an admin?
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
        username = newName;
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

    private String generateUsername(){
        // Random unique username generated when account is first created
        return "";
    }

    private String generateUniqueKey(){
        return "";
    }


}
