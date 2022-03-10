package com.bigyoshi.qrhunt;

public class Player {
    private PlayerInfo pI;
    private QRLibrary qL;
    private String playerId;

    public Player(){
        pI = new PlayerInfo();
        qL = new QRLibrary();
        uniqueKey = "I am supposed to be unique haha.";
    }

    public String getUniqueKey(){
        return this.uniqueKey;
    }

    public void deletePlayer(PlayerInfo playerToDelete, Player admin){
        PlayerInfo adminInfo = admin.getPlayerInfo();
        if (adminInfo.isAdmin()){
            // Delete Player from database
            adminInfo.deletePlayerInfo(playerToDelete);
        }
    }

    public PlayerInfo getPlayerInfo(){
        return pI;
    }
}
